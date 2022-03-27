package com.kangong.common.commontable.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kangong.common.commontable.model.CommonTableColumnVO;
import com.kangong.common.commontable.model.CommonTableVO;
import com.kangong.common.commontable.service.CommonTableService;

import lombok.extern.log4j.Log4j2;

/**
 * 1. 컬럼 공용 테이블: 테이블명, 테이블컬럼, 컬럼사이즈, 컬럼타입, 컬럼 max, 패턴, 순서
 *
 * @컬럼타입 (TEXT, NUMBER, * SELECT, DATE, USER) @2. 커스텀 테이블
 *
 * @author ktw6889
 *
 */
@Log4j2
@Controller
@RequestMapping(value = "/commontable")
public class CommonTableController {


	@Autowired
	private CommonTableService commonTableService;

	@RequestMapping(value = "/list")
	public String list(Model model, CommonTableVO masterVO,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "1") int range) throws Exception {

		try {
			// 전체 게시글 개수
			int listCnt = commonTableService.getListCnt(masterVO);

			masterVO.pageInfo(page, range, listCnt);
			model.addAttribute("pagination", masterVO);
			model.addAttribute("masterList", commonTableService.getList(masterVO));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "kims:/common/commontable/commontableList";
	}

	@RequestMapping(value = "/view")
	public String view(Model model, @RequestParam("id") String id) throws Exception {
		model.addAttribute("masterVO", commonTableService.getSelect(id));
		model.addAttribute("itemVO", new CommonTableColumnVO());
		return "kims:/common/commontable/commontableView";
	}

	@RequestMapping("/edit")
	public String edit(Model model, CommonTableVO masterVO) throws Exception {
		CommonTableVO resultVo = commonTableService.getSelect(masterVO.getId());
		if (resultVo == null || StringUtils.isEmpty(resultVo.getId())) {
			resultVo = new CommonTableVO();
		}
		model.addAttribute("masterVO", resultVo);
		return "kims:/common/commontable/commontableEdit";
	}

	@ResponseBody
	@RequestMapping(value = "/save")
	public CommonTableVO save(@RequestBody CommonTableVO masterVO) throws Exception {
		CommonTableVO returnVo = commonTableService.save(masterVO);
		return returnVo;
	}

	@RequestMapping(value = "/delete")
	public String delete(@RequestParam("id") String id) throws Exception {
		commonTableService.delete(id);
		return "kims:/common/commontable/list";
	}

	@ResponseBody
	@RequestMapping(value = "/importData")
	public CommonTableVO importData(@RequestBody CommonTableVO paramVO) throws Exception {
		log.info("importData:" + paramVO.getTableCode());
		List<CommonTableColumnVO> returnVo = commonTableService.importData(paramVO);
		CommonTableVO masterVO = new CommonTableVO();
		masterVO.setCommonTalbeColumnList(returnVo);

		return masterVO;
	}

	@RequestMapping(value = "/commonInputEdit")
	public String inputEdit(Model model, CommonTableVO paramVO) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		// column List json으로 변경

		paramVO.setParamType("input");
		List<CommonTableColumnVO> columnVOList = commonTableService.importData(paramVO);
		paramVO.setColumnList(columnVOList);
		paramVO.setJsonColumnList(objectMapper.writeValueAsString(columnVOList));
		log.info("columnList:" + objectMapper.writeValueAsString(columnVOList));
		// data List json으로 변경
		Map paramMap = new HashMap();
		paramMap.put("tableName", paramVO.getTableCode());
		// select문 만들기, columnName 정리
		StringBuffer columnName = new StringBuffer();
		for (int idx = 0; idx < columnVOList.size(); idx++) {
			columnName.append(columnVOList.get(idx).getColumnCode());
			if (idx != columnVOList.size() - 1) {
				columnName.append(",");
			}
		}
		paramMap.put("selectColumn", columnName.toString());

		List<Map> dataList = commonTableService.tableDataList(paramMap);
		paramVO.setDataList(dataList);
		paramVO.setJsonDataList(objectMapper.writeValueAsString(dataList));
		paramVO.setJsonData(objectMapper.writeValueAsString(paramVO));
		log.info("dataList:" + objectMapper.writeValueAsString(dataList));
		log.info("paramVO:" + objectMapper.writeValueAsString(paramVO));

		model.addAttribute("masterVO", paramVO);

		return "kims:/common/commontable/commonInputEdit";
	}

	@ResponseBody
	@RequestMapping(value = "/saveInput")
	public CommonTableVO saveInput(@RequestBody Map<String, Object> paramMap) throws Exception {
		commonTableService.saveInput(paramMap);
		return new CommonTableVO();
	}
}
