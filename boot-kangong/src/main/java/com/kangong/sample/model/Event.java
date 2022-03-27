package com.kangong.sample.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Event {
	private String name;
	private int limitOfEnrollment;
	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;  
} 
