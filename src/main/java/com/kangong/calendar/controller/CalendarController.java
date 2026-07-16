package com.kangong.calendar.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.kangong.calendar.google.GoogleCalendarConfig;
import com.kangong.calendar.google.GoogleCalendarService;
import com.kangong.calendar.model.ScheduleVO;
import com.kangong.calendar.service.CalendarService;
import com.kangong.common.util.SecurityContextUtil;

import lombok.extern.log4j.Log4j2;

@RequestMapping("/calendar")
@Log4j2
@Controller
public class CalendarController {

	@Autowired
	CalendarService calendarService;

	@Autowired
	GoogleCalendarConfig googleCalendarConfig;

	@Autowired
	GoogleCalendarService googleCalendarService;

	@RequestMapping(value = "/list")
	public String getCanlendarList(Model model, ScheduleVO scheduleVO) throws Exception {
		String userId = SecurityContextUtil.getLoginUserId();
		boolean googleConnected = googleCalendarService.isAuthorized(userId);

		model.addAttribute("scheduleList", calendarService.getScheduleList(scheduleVO));
		model.addAttribute("googleConnected", googleConnected);
		return "kims:/calendar/calendarList";
	}

	@ResponseBody
	@RequestMapping(value = "/getList")
	public List<ScheduleVO> getCanlendarListData(Model model, ScheduleVO scheduleVO) throws Exception {
		return calendarService.getScheduleList(scheduleVO);
	}

	@ResponseBody
	@RequestMapping(value = "/save")
	public ScheduleVO saveSchedule(@RequestBody ScheduleVO scheduleVO) throws Exception {
		String userId = SecurityContextUtil.getLoginUserId();
		calendarService.saveSchedule(scheduleVO);

		if (googleCalendarService.isAuthorized(userId)) {
			try {
				if (scheduleVO.getGoogleEventId() != null) {
					googleCalendarService.updateGoogleEvent(userId, scheduleVO);
				} else {
					String googleEventId = googleCalendarService.createGoogleEvent(userId, scheduleVO);
					scheduleVO.setGoogleEventId(googleEventId);
					calendarService.saveSchedule(scheduleVO);
				}
			} catch (Exception e) {
				log.warn("Google Calendar 동기화 실패: {}", e.getMessage());
			}
		}

		return scheduleVO;
	}

	@RequestMapping(value = "/delete")
	public String deleteSchedule(@RequestParam("id") String id) throws Exception {
		String userId = SecurityContextUtil.getLoginUserId();

		ScheduleVO vo = new ScheduleVO();
		vo.setId(id);
		ScheduleVO existing = calendarService.getSchedule(vo);

		if (existing != null && existing.getGoogleEventId() != null
				&& googleCalendarService.isAuthorized(userId)) {
			try {
				googleCalendarService.deleteGoogleEvent(userId, existing.getGoogleEventId());
			} catch (Exception e) {
				log.warn("Google Calendar 삭제 실패: {}", e.getMessage());
			}
		}

		calendarService.deleteSchedule(id);
		return "redirect:/calendar/list";
	}

	@RequestMapping(value = "/google/connect")
	public String googleConnect() {
		String authUrl = googleCalendarConfig.buildAuthorizationUrl();
		return "redirect:" + authUrl;
	}

	@RequestMapping(value = "/google/callback")
	public String googleCallback(@RequestParam("code") String code) throws Exception {
		String userId = SecurityContextUtil.getLoginUserId();

		TokenResponse tokenResponse = googleCalendarConfig.getFlow()
				.newTokenRequest(code)
				.setRedirectUri(googleCalendarConfig.getRedirectUri())
				.execute();

		googleCalendarConfig.getFlow()
				.createAndStoreCredential(tokenResponse, userId);

		log.info("Google Calendar 인증 완료 (userId={})", userId);
		return "redirect:/calendar/list";
	}

	@ResponseBody
	@RequestMapping(value = "/google/status")
	public Map<String, Object> googleStatus() {
		String userId = SecurityContextUtil.getLoginUserId();
		Map<String, Object> result = new HashMap<>();
		result.put("connected", googleCalendarService.isAuthorized(userId));
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/google/sync")
	public Map<String, Object> googleSync() throws Exception {
		String userId = SecurityContextUtil.getLoginUserId();
		Map<String, Object> result = new HashMap<>();

		if (!googleCalendarService.isAuthorized(userId)) {
			result.put("success", false);
			result.put("message", "Google 인증이 필요합니다.");
			return result;
		}

		List<ScheduleVO> googleEvents = googleCalendarService.getGoogleEvents(userId);
		int imported = 0;

		for (ScheduleVO gEvent : googleEvents) {
			ScheduleVO existing = calendarService.findByGoogleEventId(gEvent.getGoogleEventId());
			if (existing == null) {
				gEvent.setCalendarId("2");
				gEvent.setCategory("time");
				calendarService.saveSchedule(gEvent);
				imported++;
			}
		}

		result.put("success", true);
		result.put("imported", imported);
		result.put("total", googleEvents.size());
		result.put("message", "Google Calendar에서 " + imported + "건 가져옴 (전체 " + googleEvents.size() + "건)");

		log.info("Google Calendar 동기화 완료: {}건 신규 가져옴", imported);
		return result;
	}

	@RequestMapping(value = "/test")
	public String getCalendarTest(Model model) throws Exception {
		return "kims:/calendar/calendarTest";
	}

	@RequestMapping(value = "/bootstrapTest")
	public String getBootstrapTest(Model model) throws Exception {
		return "kims:/calendar/bootstrapTest";
	}
}
