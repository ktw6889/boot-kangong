package com.kangong.calendar.google;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.kangong.calendar.model.ScheduleVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class GoogleCalendarService {

    @Autowired
    private GoogleCalendarConfig googleCalendarConfig;

    private Calendar buildCalendarClient(String userId) throws Exception {
        Credential credential = googleCalendarConfig.getFlow()
                .loadCredential(userId);

        if (credential == null || credential.getAccessToken() == null) {
            throw new IllegalStateException("Google 인증이 필요합니다.");
        }

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("kangong-calendar")
                .build();
    }

    public boolean isAuthorized(String userId) {
        try {
            Credential credential = googleCalendarConfig.getFlow()
                    .loadCredential(userId);
            return credential != null && credential.getAccessToken() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public List<ScheduleVO> getGoogleEvents(String userId) throws Exception {
        Calendar calendarClient = buildCalendarClient(userId);
        List<ScheduleVO> result = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        DateTime timeMin = toGoogleDateTime(now.minusMonths(3));
        DateTime timeMax = toGoogleDateTime(now.plusMonths(3));

        Events events = calendarClient.events().list("primary")
                .setTimeMin(timeMin)
                .setTimeMax(timeMax)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .setMaxResults(250)
                .execute();

        if (events.getItems() != null) {
            for (Event event : events.getItems()) {
                ScheduleVO vo = new ScheduleVO();
                vo.setGoogleEventId(event.getId());
                vo.setTitle(event.getSummary());
                vo.setComments(event.getDescription());
                vo.setCategory("time");
                vo.setCalendarId("2");

                if (event.getStart() != null) {
                    vo.setStartDate(toLocalDateTime(event.getStart()));
                }
                if (event.getEnd() != null) {
                    vo.setEndDate(toLocalDateTime(event.getEnd()));
                }

                result.add(vo);
            }
        }

        log.info("Google Calendar에서 {}건 이벤트 조회 (userId={})", result.size(), userId);
        return result;
    }

    public String createGoogleEvent(String userId, ScheduleVO vo) throws Exception {
        Calendar calendarClient = buildCalendarClient(userId);

        Event event = new Event();
        event.setSummary(vo.getTitle());
        event.setDescription(vo.getComments());
        event.setStart(toEventDateTime(vo.getStartDate()));
        event.setEnd(toEventDateTime(vo.getEndDate()));

        Event created = calendarClient.events()
                .insert("primary", event)
                .execute();

        log.info("Google Calendar 이벤트 생성: {}", created.getId());
        return created.getId();
    }

    public void updateGoogleEvent(String userId, ScheduleVO vo) throws Exception {
        if (vo.getGoogleEventId() == null) return;

        Calendar calendarClient = buildCalendarClient(userId);

        Event event = calendarClient.events()
                .get("primary", vo.getGoogleEventId())
                .execute();

        event.setSummary(vo.getTitle());
        event.setDescription(vo.getComments());
        event.setStart(toEventDateTime(vo.getStartDate()));
        event.setEnd(toEventDateTime(vo.getEndDate()));

        calendarClient.events()
                .update("primary", vo.getGoogleEventId(), event)
                .execute();

        log.info("Google Calendar 이벤트 수정: {}", vo.getGoogleEventId());
    }

    public void deleteGoogleEvent(String userId, String googleEventId) throws Exception {
        if (googleEventId == null) return;

        Calendar calendarClient = buildCalendarClient(userId);
        calendarClient.events()
                .delete("primary", googleEventId)
                .execute();

        log.info("Google Calendar 이벤트 삭제: {}", googleEventId);
    }

    private EventDateTime toEventDateTime(LocalDateTime ldt) {
        if (ldt == null) return null;
        long millis = ldt.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        return new EventDateTime()
                .setDateTime(new DateTime(millis))
                .setTimeZone("Asia/Seoul");
    }

    private DateTime toGoogleDateTime(LocalDateTime ldt) {
        long millis = ldt.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        return new DateTime(millis);
    }

    private LocalDateTime toLocalDateTime(EventDateTime edt) {
        DateTime dt = edt.getDateTime();
        if (dt == null) dt = edt.getDate();
        if (dt == null) return null;
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(dt.getValue()),
                ZoneId.of("Asia/Seoul"));
    }
}
