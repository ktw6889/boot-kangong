function addTableRow(tableId) {
	if(!tableId) tableId = 'add_Table';
	$("#"+tableId).find("#blockTbody tr[trName='emptyRow']").remove();
	$("#"+tableId+"_none").css("display","none");	

	var trObj = $("#"+tableId+" #templateTbody tr").get(0);
	if(trObj != null){
		var attrList = trObj.attributes;
		var jqueryAttr = '';
		for (var i=0; i<attrList.length; i++){
			jqueryAttr = attrList.item(i).nodeName;
			if(jqueryAttr.indexOf("jQuery") > -1){
				$(trObj).removeAttr(jqueryAttr);
			}
		}	
	}

	//$("#"+tableId+" #templateTbody tr").removeAttr(jQuery.expando);	
	$("#"+tableId).find("#blockTbody").append($("#"+tableId+" #templateTbody").html());

	try {
		if(addTableRowCallBack) addTableRowCallBack();
	} catch(e) {}

}


function deleteTableRow(tableId, checkboxName) {
	if(!tableId) tableId = 'add_Table';
	if(!checkboxName) checkboxName = 'checkBoxList';
	$("#"+tableId).find("#blockTbody").find("input[name='"+checkboxName+"']:checked").each(function (i){

		if($(this).next().val() == 'C') {
			$(this).parents("tr:first").remove();
		} else {
			$(this).next().val('D');
			$(this).parents("tr:first").each(function (j) {
				$(this).attr("trstatus","TD_NONE");
				$(this).css("display","none");
				$(this).find("input").css("display","none");
				$(this).find("select").css("display","none");
				$(this).find("div").css("display","none");
				$(this).find("textarea").css("display","none");
			});
		}

	});

	

	if($("#"+tableId).find("#blockTbody").find("input[name='"+checkboxName+"']").size() == 0) {
		$("#"+tableId+"_none").css("display","");
	} else {
		$("#"+tableId+"_none").css("display","none");
	}

	try {
		if(deleteTableRowCallBack) deleteTableRowCallBack();
	} catch(e) {}

}



function updateTableRow(obj, checkboxName) {

	if(!checkboxName) checkboxName = 'checkBoxList';

	$(obj).parents("tr:first").each(function (i) {

		if($(this).find("input[name='"+checkboxName+"']").next().val() != 'C') {

			$(this).find("input[name='"+checkboxName+"']").next().val('U');

			$(this).addClass("Retouch");

			$(this).find("input").addClass("Retouch");

			$(this).find("select").addClass("Retouch");

			$(this).find("div").addClass("Retouch");

			$(this).find("textarea").addClass("Retouch");

		}

	});

	try {

		if(updateTableRowCallBack) updateTableRowCallBack();

	} catch(e) {}

}

function addTableRowList(tableId, addTable_addRowLength) {
	if(!tableId) tableId = 'add_Table';
	$("#"+tableId).find("#blockTbody tr[trName='emptyRow']").remove();
	$("#"+tableId+"_none").css("display","none");	

	var trObj = $("#"+tableId+" #templateTbody tr").get(0);

	if(trObj != null){
		var attrList = trObj.attributes;
		var jqueryAttr = '';
		for (var i=0; i<attrList.length; i++){
			jqueryAttr = attrList.item(i).nodeName;

			if(jqueryAttr.indexOf("jQuery") > -1){
				$(trObj).removeAttr(jqueryAttr);
			}
		}	
	}
		
	//alert($("#"+tableId+" #templateTbody").html());
	//alert("abcdefg$TR_NUM$hijklmn".replace('$TR_NUM$',addTable_addRowLength++));
	//alert(($("#"+tableId+" #templateTbody").html()).replace(/TEMPLATE_TR_NUM/gi,addTable_addRowLength++));
	//alert(($("#"+tableId+" #templateTbody").html()).split('$TR_NUM$').join(addTable_addRowLength++));

	$("#"+tableId).find("#blockTbody").append($("#"+tableId+" #templateTbody").html().replace(/TEMPLATE_TR_NUM/gi,addTable_addRowLength));
	
	try {
		if(addTableRowCallBack) addTableRowCallBack();
	} catch(e) {}

}