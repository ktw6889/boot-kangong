package com.kangong.common.dd.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.kangong.common.dd.model.ComDdVO;
import com.kangong.common.dd.model.ComDdValueVO;
import com.kangong.common.service.CommonService;

@Service
public class DdService extends CommonService {

	public List<ComDdVO> getList(ComDdVO ddVO) throws Exception {
		return sqlSession.selectList("kangong.dd.list", ddVO);
	}

	// 총 게시글 개수 확인
	public int getListCnt(ComDdVO ddVO) throws Exception {
		return sqlSession.selectOne("kangong.dd.listCnt", ddVO);
	}

	public ComDdVO getSelect(String id) throws Exception {
		ComDdVO ddVO = sqlSession.selectOne("kangong.dd.select", id);
		if (ddVO != null && !ObjectUtils.isEmpty(ddVO.getId()))
			ddVO.setDdValueList(getListItem(id));
		return ddVO;
	}

	public ComDdVO save(ComDdVO ddVO) throws Exception {
		if (ObjectUtils.isEmpty(ddVO.getId())) {
			String id = getNewId("ST_COM_DD");
			ddVO.setId(id);
			insert(ddVO);
		} else {
			update(ddVO);
		}

		saveItem(ddVO);
		return ddVO;
	}

	public void insert(ComDdVO ddVO) throws Exception {
		sqlSession.insert("kangong.dd.insert", ddVO);
	}

	public void update(ComDdVO ddVO) throws Exception {
		sqlSession.update("kangong.dd.update", ddVO);
	}

	public void delete(String id) throws Exception {
		sqlSession.update("kangong.dd.delete", id);
	}

	public List<ComDdValueVO> getListItem(String masterId) throws Exception {
		return sqlSession.selectList("kangong.dd.listItem", masterId);
	}

	public void saveItem(ComDdVO ddVO) throws Exception {
		for (ComDdValueVO itemVO : ddVO.getDdValueList()) {
			if ("I".equals(itemVO.getRowFlag())) {
				String id = getNewId("ST_COM_DD_VALUE");
				itemVO.setId(id);
				itemVO.setDdId(ddVO.getId());
				insertItem(itemVO);
			} else if ("U".equals(itemVO.getRowFlag())) {
				updateItem(itemVO);
			} else if ("D".equals(itemVO.getRowFlag())) {
				deleteItem(itemVO.getId());
			}
		}
	}

	public void insertItem(ComDdValueVO ddValueVO) throws Exception {
		sqlSession.insert("kangong.dd.insertItem", ddValueVO);
	}

	public void updateItem(ComDdValueVO ddValueVO) throws Exception {
		sqlSession.update("kangong.dd.updateItem", ddValueVO);
	}

	public void deleteItem(String id) throws Exception {
		sqlSession.update("kangong.dd.deleteItem", id);
	}

}
