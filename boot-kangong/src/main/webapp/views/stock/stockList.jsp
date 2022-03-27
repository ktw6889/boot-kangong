<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>

<li> <a href="javascript:kangong.stock.dailyUpdate();"> Daily Update </a> </li>
<li> <a href="javascript:kangong.stock.save('005930');"> 삼성전자 </a> </li>
<li> <a href="javascript:kangong.stock.saveAll();"> 전체List 저장 </a> </li>
<li> <a href="javascript:kangong.stock.finanacialSave('017670');"> SK텔레콤 Financial 저장 </a> </li>
<li> <a href="javascript:kangong.stock.finanacialSaveAll();"> 전체List Financial 저장 </a> </li>
<li> <a href="javascript:kangong.stock.finanacial2Save('005930');"> 삼성전자 Financial2 저장 </a> </li>
<li> <a href="javascript:kangong.stock.dailyPriceSave('005930');"> 삼성전자 dailyPrice 저장 </a> </li>
<li> <a href="javascript:kangong.stock.dailyPriceSaveAll();"> 전체 dailyPrice 저장 </a> </li>
<li> <a href="javascript:kangong.stock.seleniumFinancialSave('005930');"> 삼성전자 Seleninum Financial 저장 </a> </li>
<li> <a href="javascript:kangong.stock.seleniumFinancialAnalysisSave('005930');">삼성전자 Seleninum Financial Analysis 저장 </a> </li>
<li> <a href="javascript:kangong.stock.seleniumFinancialAllSave();"> 전체 Seleninum Financial 저장 </a> </li>
<li> <a href="javascript:kangong.stock.seleniumFinancialAnalysisSaveAll();"> 전체 Seleninum Financial Analysis 저장 </a> </li>


<c:url var="saveURL" value="/stock/save"></c:url>
<c:url var="saveAllURL" value="/stock/list/save"></c:url>
<c:url var="finanacialSaveURL" value="/stock/financial/save"></c:url>
<c:url var="finanacialSaveAllURL" value="/stock/financial/saveAll"></c:url>
<c:url var="finanacial2SaveURL" value="/stock/financial2/save"></c:url>
<c:url var="dailyPriceSaveURL" value="/stock/dailyPrice/save"></c:url>
<c:url var="dailyPriceSaveAllURL" value="/stock/dailyPrice/saveAll"></c:url>
<c:url var="seleniumFinancialSaveURL" value="/stock/selenium/financial/save"></c:url>
<c:url var="seleniumFinancialSaveAllURL" value="/stock/selenium/financial/saveAll"></c:url>
<c:url var="seleniumFinancialAnalysisSaveURL" value="/stock/selenium/financialAnalysis/save"></c:url>
<c:url var="seleniumFinancialAnalysisSaveAllURL" value="/stock/selenium/financialAnalysis/saveAll"></c:url>
<c:url var="dailyUpdateURL" value="/stock/daily/update"></c:url>


<script>
$( document ).ready(function() {

});

kangong.stock = {
	save :function (id){
			var paramObj ={};
			paramObj.stockId =  id;

			kangong.form.submitPost('${saveURL}',paramObj);
		}
	,saveAll :function (){
		kangong.form.submitPost('${saveAllURL}');
	}
	,finanacialSave :function (id){
		var paramObj ={};
		paramObj.stockId =  id;
		
		kangong.form.submitPost('${finanacialSaveURL}',paramObj);
	}
	,finanacialSaveAll :function (){
		kangong.form.submitPost('${finanacialSaveAllURL}');
	}
	,finanacial2Save :function (id){
		var paramObj ={};
		paramObj.stockId =  id;
		
		kangong.form.submitPost('${finanacial2SaveURL}',paramObj);
	}
	,dailyPriceSave :function (id){
		var paramObj ={};
		paramObj.stockId =  id;
		
		kangong.form.submitPost('${dailyPriceSaveURL}',paramObj);
	}
	,dailyPriceSaveAll :function (){
		kangong.form.submitPost('${dailyPriceSaveAllURL}');
	}
	,seleniumFinancialSave :function (id){
		var paramObj ={};
		paramObj.stockId =  id;
		
		kangong.form.submitPost('${seleniumFinancialSaveURL}',paramObj);
	}
	,seleniumFinancialAllSave :function (){
		kangong.form.submitPost('${seleniumFinancialSaveAllURL}');
	}
	,seleniumFinancialAnalysisSave :function (id){
		var paramObj ={};
		paramObj.stockId =  id;
		
		kangong.form.submitPost('${seleniumFinancialAnalysisSaveURL}',paramObj);
	}
	,seleniumFinancialAnalysisSaveAll :function (){
		kangong.form.submitPost('${seleniumFinancialAnalysisSaveAllURL}');
	}
	,dailyUpdate :function (){
		kangong.form.submitPost('${dailyUpdateURL}');
	}
}

</script>