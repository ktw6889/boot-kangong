package com.kangong.common.util;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.kangong.common.model.MessageVo;
import com.kangong.common.service.CommonService;

public class DatabaseMessageSource extends ReloadableResourceBundleMessageSource {

	@Autowired
	private CommonService commonService;

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {

		MessageVo messageVo = commonService.getMessageBundle(code, locale);

		MessageFormat format;

		if (messageVo != null && messageVo.getId() != null) {

			format = new MessageFormat(messageVo.getValue(), locale);

		} else {

			format = super.resolveCode(code, locale);

		}

		return format;

	}

	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {

		MessageVo messageVo = commonService.getMessageBundle(code, locale);

		String format;

		if (messageVo != null && messageVo.getId() != null) {

			format = messageVo.getValue();

		} else {

			format = super.resolveCodeWithoutArguments(code, locale);

		}

		return format;

	}

}
