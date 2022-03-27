package com.kangong.sample.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kangong.common.annotation.LogExecutionTime;
import com.kangong.common.util.CommonUtil;
import com.kangong.sample.model.SampleDTO;
import com.kangong.sample.service.SampleService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class SampleController {
	
	@Autowired
	SampleService sampleService;
	
	@Autowired
	CommonUtil commonUtil;
	
	@GetMapping("/sample")
	public String sample() {		
		
		System.out.println("sample====");
		return "kims:/common/sample";
	}	
	
	@LogExecutionTime
	@GetMapping("/main")
	public String main() {				
		System.out.println("main====");
		//return "/views/common/main.jsp";
		return "common/main";
	}
	
	@GetMapping("/events")
	public String events(Model model) {
		System.out.println("evnents");
		 model.addAttribute("events",sampleService.getEvents());
		 return "thymeleaf/events";
	}
	
	@GetMapping("/ex2")
	public String exModel(Model model) {
		log.info("ex2==========");
		List<SampleDTO> list = IntStream.range(1, 20).asLongStream().mapToObj(
		  i -> {
			SampleDTO dto = SampleDTO.builder()
					.sno(i)
					.first("First..."+i)
					.last("Last..."+i)
					.regTime(LocalDateTime.now())
					.build();
			return dto;
		  }).collect(Collectors.toList());
		
		model.addAttribute("list",list);
		return "thymeleaf/ex2";
	}
	
	@RequestMapping(value = "/util/getTableQuery")
	public String getTableQuery(@RequestParam Map<String, String> param) throws Exception {
		String tableName = param.get("tableName");
		List<Map<String, String>> list = commonUtil.getColumnInfo(tableName);	
		commonUtil.printColumn(tableName, list);

		return "common/main";
	}

}
