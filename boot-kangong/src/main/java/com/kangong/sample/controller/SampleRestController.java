package com.kangong.sample.controller;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kangong.sample.service.SampleService;

@RestController
public class SampleRestController {

	// (1) Logger Name이 "com.kangong.sample.controller.com.kangong.sample.controller.SampleRestController"인 Logger 설정을 따르는 Logger 객체 생성
	private Logger logger = LogManager.getLogger();	
	// (2) 위와 동일 
	Logger logger2 = LogManager.getLogger(SampleRestController.class);  
	// (3) Logger Name이 "X"인 Logger설정을 따르는 Logger 객체 생성
	Logger logger3 = LogManager.getLogger("X");  
	
	/**
	 * <Loggers>
	  <!-- attribute: name(Logger명), level(Log Level), additivity(중복로깅여부, true or false) -->
	  <!-- element: AppenderRef(Appender명) -->
	  <Logger name="X.Y" level="INFO" additivity="false">
	   <AppenderRef ref="console"/>  
	  </Logger>
	  <Logger name="X" level="DEBUG" additivity="false">
	   <AppenderRef ref="console"/>  
	  </Logger>
	  <Rootlevel="ERROR">
	   <AppenderRef ref="console"/>
	  </Root>
	 </Loggers>
	 * 
	 */
	
	@Autowired
	SampleService sampleService;
	
	
	
	@CrossOrigin
	@GetMapping("/hello")
	public String[] hello() {		
		
		
		return new String[] {"Hello","World",sampleService.gettNow()};
	}
	
	
	@CrossOrigin
	@GetMapping("/jsonList")
	@ResponseBody
	public JSONArray hello2() {		
		
		JSONArray jsonArray = new JSONArray();
		
		JSONObject obj1 = new JSONObject();
		obj1.put("product_name", "기계식키보드");
		obj1.put("price", "25000");
		obj1.put("category", "노트북/태블릿");
		obj1.put("delivery_price", "5000");
		jsonArray.add(obj1);
		
		JSONObject obj2 = new JSONObject();
		obj2.put("product_name", "무선마우스");
		obj2.put("price", "35000");
		obj2.put("category", "노트북/태블릿");
		obj2.put("delivery_price", "4000");
		jsonArray.add(obj2);
		
		JSONObject obj3 = new JSONObject();
		obj3.put("product_name", "아이패드");
		obj3.put("price", "525000");
		obj3.put("category", "노트북/태블릿");
		obj3.put("delivery_price", "2000");
		jsonArray.add(obj3);
		
		JSONObject obj4 = new JSONObject();
		obj4.put("product_name", "무선키보드");
		obj4.put("price", "125000");
		obj4.put("category", "노트북/태블릿");
		obj4.put("delivery_price", "1000");
		jsonArray.add(obj4);
		
		
		logger.info("jsonList:"+jsonArray); 
		
		return jsonArray;
	}
	
	@CrossOrigin
	@PostMapping("/helloPost")
	public HashMap<String, String> helloPost(@RequestBody HashMap<String, String> paramMap) {	
		
		logger.info("helloPost==============="); 
		return paramMap;
	}
	
	@CrossOrigin
	@PutMapping("/helloPut")
	public HashMap<String, String> helloPut(@RequestBody HashMap<String, String> paramMap) {	
		
		logger.info("helloPut==============="); 
		return paramMap;
	}
	
	@CrossOrigin
	@PatchMapping("/helloPatch")
	public HashMap<String, String> helloPatch(@RequestBody HashMap<String, String> paramMap) {	
		
		logger.info("helloPatch==============="); 
		return paramMap;
	}
	
	@CrossOrigin
	@DeleteMapping("/helloDelete")
	public HashMap<String, String> helloDelete(@RequestBody HashMap<String, String> paramMap) {	
		
		logger.info("helloDelete==============="); 
		return paramMap;
	}

}
