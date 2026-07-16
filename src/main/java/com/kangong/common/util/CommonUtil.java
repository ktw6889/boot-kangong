package com.kangong.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kangong.common.service.CommonService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CommonUtil {

	@Autowired
	private CommonService commonService;

	public String getNewId(String tableName) throws Exception {
		return commonService.getNewId(tableName);
	}
}
