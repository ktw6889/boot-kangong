package com.kangong.calendar.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.kangong.calendar.model.ScheduleVO;
import com.kangong.common.service.CommonService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service("calendarService")
public class CalendarService extends CommonService {

	public ScheduleVO getSchedule(ScheduleVO vo) throws Exception {
		ScheduleVO resultVo = new ScheduleVO();
		if (!ObjectUtils.isEmpty(vo.getId()))
			resultVo = sqlSession.selectOne("kangong.schedule.select", vo);
		return resultVo;
	}
	
	public List<ScheduleVO> getScheduleList(ScheduleVO vo) throws Exception {
		return sqlSession.selectList("kangong.schedule.select", vo);		
	}
	
	public void saveSchedule(ScheduleVO scheduleVO) throws Exception {
		if (scheduleVO != null && ObjectUtils.isEmpty(scheduleVO.getId())) {
			log.info("kangong.schedule.insert");
			String id = getNewId("ST_SCHEDULE");
			scheduleVO.setId(id);
			sqlSession.insert("kangong.schedule.insert", scheduleVO);
		} else {
			log.info("kangong.schedule.update");
			sqlSession.update("kangong.schedule.update", scheduleVO);
		}
	}
	
	public void deleteSchedule(String id) throws Exception{
		if (!ObjectUtils.isEmpty(id)) {
			sqlSession.delete("kangong.schedule.delete", id);
		}
			
	}
	
	
}
