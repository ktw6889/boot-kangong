package com.kangong.common.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kangong.common.model.MessageVo;
import com.kangong.common.model.RowFlagItem;


@Transactional
@Service
public class CommonService {

	@FunctionalInterface
	public interface CheckedConsumer<T> {
		void accept(T item) throws Exception;
	}

	@Autowired
	public SqlSession sqlSession;

	public String getNewId(String tableName) throws Exception {
		return sqlSession.selectOne("kangong.com.idGen", tableName);
	}

	public <T extends RowFlagItem> void processRowFlagItems(List<T> items, String tableName,
			CheckedConsumer<T> onInsert, CheckedConsumer<T> onUpdate, CheckedConsumer<T> onDelete) throws Exception {
		for (T item : items) {
			String flag = item.getRowFlag();
			if ("I".equals(flag)) {
				item.setId(getNewId(tableName));
				onInsert.accept(item);
			} else if ("U".equals(flag)) {
				onUpdate.accept(item);
			} else if ("D".equals(flag)) {
				onDelete.accept(item);
			}
		}
	}

	public MessageVo getMessageBundle(String messageCode, Locale locale) {
		MessageVo resultVo = sqlSession.selectOne("kangong.com.selectMessage", messageCode);
		if ("en".equals(locale.getLanguage())) {
			resultVo.setValue(resultVo.getLableEn());
		} else {
			resultVo.setValue(resultVo.getLableKo());
		}

		return resultVo;
	}

	public List<Map<String, String>> getColumnInfo(String tableName) throws Exception {
		List<Map<String, String>> resultList = sqlSession.selectList("kangong.com.selectColumn", tableName);
		return resultList;
	}

}
