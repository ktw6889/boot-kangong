package com.kangong.calendar.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.kangong.common.model.CommonVO;

public class ScheduleVO extends CommonVO{

	private String category; //CATEGORY
	private String title; //제목
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startDate; //시작일
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endDate; //종료일
	
	private String comments; //일정_설명
	
	private String calendarId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime start; //시작일
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime end; //종료일
	
	
	public String getCalendarId() {
		return calendarId;
	}	
	

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}



	public LocalDateTime getStart() {
		return startDate;
	}

	public LocalDateTime getEnd() {
		return endDate;
	}

	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public LocalDateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	public LocalDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}
