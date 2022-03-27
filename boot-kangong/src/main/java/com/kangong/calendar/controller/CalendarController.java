package com.kangong.calendar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kangong.calendar.model.ScheduleVO;
import com.kangong.calendar.service.CalendarService;

import lombok.extern.log4j.Log4j2;

@RequestMapping("/calendar")
@Log4j2
@Controller
public class CalendarController {

	@Autowired
	CalendarService calendarService;
	
	@RequestMapping(value ="/list")
	public String getCanlendarList(Model model, ScheduleVO scheduleVO) throws Exception {
		
		model.addAttribute("scheduleList", calendarService.getScheduleList(scheduleVO));
		return "kims:/calendar/calendarList";
	}
	
	@ResponseBody
	@RequestMapping(value ="/getList")
	public List<ScheduleVO> getCanlendarListData(Model model, ScheduleVO scheduleVO) throws Exception {
		
		return calendarService.getScheduleList(scheduleVO);
	}
	
	@ResponseBody
	@RequestMapping(value = "/save")
	public ScheduleVO saveSchedule(@RequestBody ScheduleVO scheduleVO) throws Exception {
		calendarService.saveSchedule(scheduleVO);
		return scheduleVO;
	}
	
	@RequestMapping(value = "/delete")
	public String deleteBoard(@RequestParam("id") String id) throws Exception {
		calendarService.deleteSchedule(id);
		return "redirect:/calendar/list";
	}
	
	@RequestMapping(value ="/test")
	public String getCalendarTest(Model model) throws Exception {
		
		return "kims:/calendar/calendarTest";
	}
	
	@RequestMapping(value ="/bootstrapTest")
	public String getBootstrapTest(Model model) throws Exception {
		
		return "kims:/calendar/bootstrapTest";
	}
}
