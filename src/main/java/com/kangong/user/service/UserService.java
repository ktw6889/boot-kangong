package com.kangong.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.kangong.common.service.CommonService;
import com.kangong.user.model.UserItemVO;
import com.kangong.user.model.UserVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("userService")
public class UserService extends CommonService {

	public UserVO getUserInfo(UserVO userVo) throws Exception {
		// 사용자 정보 가져오기
		UserVO resultVo = getUser(userVo);
		// 취미
		if (resultVo.getHobby() != null)
			resultVo.setStrsHobby(resultVo.getHobby().split(","));

		// item 정보 가져오기
		resultVo.setUserItemList(getUserItemList(resultVo));

		return resultVo;
	}

	public UserVO getUser(UserVO vo) throws Exception {
		UserVO resultVo = new UserVO();
		if (!ObjectUtils.isEmpty(vo.getId()))
			resultVo = sqlSession.selectOne("kangong.user.select", vo);
		return resultVo;
	}

	public List<UserItemVO> getUserItemList(UserVO vo) throws Exception {
		List<UserItemVO> userItemList = sqlSession.selectList("kangong.user.itemSelect", vo);
		return userItemList;
	}

	public List<UserVO> getUserList(UserVO vo) throws Exception {
		List<UserVO> list = sqlSession.selectList("kangong.user.select", vo);
		return list;
	}

	public void saveUser(UserVO userVo) throws Exception {
		// userInfo 저장
		userVo.setHobby(userVo.convertStrsHooby());

		if (userVo != null && ObjectUtils.isEmpty(userVo.getId())) {
			log.info("kangong.user.insert");
			String id = sqlSession.selectOne("kangong.user.idGen", "ST_USER_INFO");
			userVo.setId(id);
			sqlSession.insert("kangong.user.insert", userVo);
		} else {
			log.info("kangong.user.update");
			sqlSession.update("kangong.user.update", userVo);
		}

		// item 저장
		saveUserItem(userVo);
	}

	public void saveUserItem(UserVO userVo) throws Exception {
		List<UserItemVO> userItemList = userVo.getUserItemList();
		log.info("userItemList.size(): {}", userItemList.size());

		for (UserItemVO item : userItemList) {
			item.setApplyModule(item.convertStrsApplyModule());
			log.info("projectName: {}:{}", item.getProjectName(), item.getRowFlag());
		}

		processRowFlagItems(userItemList, "ST_USER_INFO_ITEM",
			item -> {
				item.setUserInfoId(userVo.getId());
				sqlSession.insert("kangong.user.itemInsert", item);
			},
			item -> sqlSession.update("kangong.user.itemUpdate", item),
			item -> sqlSession.update("kangong.user.itemDelete", item)
		);
	}
	
	public void saveUserSimple(UserVO userVo) throws Exception {
		// userInfo 저장
		if (userVo != null && ObjectUtils.isEmpty(userVo.getId())) {
			log.info("kangong.user.insertSimple");
			String id = sqlSession.selectOne("kangong.user.idGen", "ST_USER_INFO");
			userVo.setId(id);
			sqlSession.insert("kangong.user.insertSimple", userVo);
		} else {
			log.info("kangong.user.updateSimple");
			sqlSession.update("kangong.user.updateSimple", userVo);
		}

		// item 저장
		saveUserItem(userVo);
	}

}
