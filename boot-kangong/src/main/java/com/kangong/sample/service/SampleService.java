package com.kangong.sample.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kangong.sample.model.Event;

@Service
public class SampleService {

	@Autowired
	private SqlSession sqlSession;
	
	public String gettNow() {
        return sqlSession.selectOne("kangong.test.selectNow");
    }
	
	public List<Event> getEvents(){ 
		  Event event = Event.builder() 
				  .name("스프링 웹 MVC 스터디 1차") 
				  .limitOfEnrollment(5)
				  .startDateTime(LocalDateTime.of(2020, 12, 6, 12 , 47))
				  .endDateTime(LocalDateTime.of(2020, 12, 6, 15 , 47)) .build();
	  
		  Event event2 = Event.builder() 
				  .name("스프링 웹 MVC 스터디 2차")
				  .limitOfEnrollment(5) 
				  .startDateTime(LocalDateTime.of(2020, 12, 10, 12 , 47))
				  .endDateTime(LocalDateTime.of(2020, 12, 10, 15 , 47)) .build();
		   
		  return List.of(event, event2); 	  
	  }	 
}
