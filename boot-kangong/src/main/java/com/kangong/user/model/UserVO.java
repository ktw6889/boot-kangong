package com.kangong.user.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.kangong.common.model.CommonVO;

public class UserVO extends CommonVO {

	private String userId;
	private String userName;
	private String password;
	private String age;
	private Date birthDate;
	private String city;
	private String hobby;
	private String comment;
	private String gender;

	private String[] strsHobby;

	private List<UserItemVO> userItemList = new ArrayList();

	public String convertStrsHooby() {
		return StringUtils.arrayToCommaDelimitedString(strsHobby);
	}

	public List<UserItemVO> getUserItemList() {
		return userItemList;
	}

	public void setUserItemList(List<UserItemVO> userItemList) {
		this.userItemList = userItemList;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String[] getStrsHobby() {
		return strsHobby;
	}

	public void setStrsHobby(String[] strsHobby) {
		this.strsHobby = strsHobby;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
