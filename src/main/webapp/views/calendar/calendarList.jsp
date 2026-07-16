<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/board.css" />

<link rel="stylesheet" type="text/css" href="https://uicdn.toast.com/tui-calendar/latest/tui-calendar.css" />
<link rel="stylesheet" type="text/css" href="https://uicdn.toast.com/tui.date-picker/latest/tui-date-picker.css" />
<link rel="stylesheet" type="text/css" href="https://uicdn.toast.com/tui.time-picker/latest/tui-time-picker.css" />
<link rel="stylesheet" type="text/css" href="https://nhn.github.io/tui.calendar/latest/examples/css/icons.css" />
<link rel="stylesheet" type="text/css" href="https://nhn.github.io/tui.calendar/latest/examples/css/default.css" />

<script src="https://uicdn.toast.com/tui.code-snippet/v1.5.2/tui-code-snippet.min.js"></script>
<script src="https://uicdn.toast.com/tui.time-picker/latest/tui-time-picker.min.js"></script>
<script src="https://uicdn.toast.com/tui.date-picker/latest/tui-date-picker.min.js"></script>
<script src="https://uicdn.toast.com/tui-calendar/latest/tui-calendar.js"></script>

<style>
.calendar-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    flex-wrap: wrap;
    gap: 10px;
}
.calendar-toolbar .toolbar-left {
    display: flex;
    align-items: center;
    gap: 8px;
}
.calendar-toolbar .toolbar-right {
    display: flex;
    align-items: center;
    gap: 8px;
}
.google-status {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 0.82rem;
    padding: 5px 12px;
    border-radius: 20px;
    font-weight: 500;
}
.google-status.connected {
    background: #d3f9d8;
    color: #2b8a3e;
}
.google-status.disconnected {
    background: #ffe3e3;
    color: #c92a2a;
}
.btn-google {
    background: #fff;
    border: 1px solid #dadce0;
    color: #3c4043;
    font-size: 0.82rem;
    font-weight: 500;
    padding: 6px 16px;
    border-radius: 6px;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    transition: background 0.15s;
}
.btn-google:hover {
    background: #f1f3f4;
}
.btn-google img {
    width: 16px;
    height: 16px;
}
.sync-result {
    display: none;
    margin-top: 10px;
    padding: 10px 16px;
    border-radius: 6px;
    font-size: 0.85rem;
}
.sync-result.success { background: #d3f9d8; color: #2b8a3e; display: block; }
.sync-result.error { background: #ffe3e3; color: #c92a2a; display: block; }
</style>

<div class="board-container-wide">

	<div class="board-header">
		<h2>일정</h2>
	</div>

	<!-- Google Calendar 연동 상태 -->
	<div class="calendar-toolbar">
		<div class="toolbar-left">
			<button type="button" class="btn btn-board-outline move-today" data-action="move-today">Today</button>
			<button type="button" class="btn btn-board-outline move-day" data-action="move-prev">
				<i class="calendar-icon ic-arrow-line-left" data-action="move-prev"></i>
			</button>
			<button type="button" class="btn btn-board-outline move-day" data-action="move-next">
				<i class="calendar-icon ic-arrow-line-right" data-action="move-next"></i>
			</button>
			<span id="renderRange" class="render-range" style="font-weight:600;font-size:1.1rem;margin-left:8px;"></span>
		</div>
		<div class="toolbar-right">
			<c:choose>
				<c:when test="${googleConnected}">
					<span class="google-status connected">Google 연동됨</span>
					<button type="button" class="btn-google" id="btnGoogleSync">
						<img src="https://www.gstatic.com/images/branding/product/1x/calendar_2020q4_48dp.png" alt="G"/>
						동기화
					</button>
				</c:when>
				<c:otherwise>
					<span class="google-status disconnected">Google 미연결</span>
					<a href="${pageContext.request.contextPath}/calendar/google/connect" class="btn-google">
						<img src="https://www.gstatic.com/images/branding/product/1x/calendar_2020q4_48dp.png" alt="G"/>
						Google Calendar 연결
					</a>
				</c:otherwise>
			</c:choose>
			<button class="btn btn-board-primary" id="btnCreateSchedule" data-toggle="modal" data-target="#createScheduleDiv">일정생성</button>
		</div>
	</div>

	<div id="syncResult" class="sync-result"></div>

	<div id="calendarDiv" style="height: 700px; border:1px solid #e9ecef; border-radius:8px; overflow:hidden;"></div>

</div>

<!-- 일정 생성/수정 모달 -->
<div class="modal fade" id="createScheduleDiv" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content" style="border-radius:10px;overflow:hidden;">
      <div class="modal-header" style="background:#343a40;color:#fff;border:none;">
        <h5 class="modal-title" style="font-weight:700;">일정</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close" style="color:#fff;opacity:0.8;">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body" style="padding:24px;">
          <input type="hidden" name="id" />
          <input type="hidden" name="category" value="time"/>
          <input type="hidden" name="googleEventId" />
		  <div class="mb-3 row">
		    <label class="col-sm-3 col-form-label" style="font-weight:600;font-size:0.9rem;">카테고리</label>
		    <div class="col-sm-9">
		        <select class="form-control" name="calendarId">
				  <option value="">선택</option>
				  <option value="1">Family</option>
				  <option value="2">Work</option>
				  <option value="3">Friends</option>
				  <option value="4">School</option>
				  <option value="5">Etc</option>
				</select>
		    </div>
		  </div>
		  <div class="mb-3 row">
		    <label class="col-sm-3 col-form-label" style="font-weight:600;font-size:0.9rem;">제목</label>
		    <div class="col-sm-9">
		      <input type="text" class="form-control" name="title" id="inputTitle" placeholder="일정 제목">
		    </div>
		  </div>
		  <div class="mb-3 row">
		    <label class="col-sm-3 col-form-label" style="font-weight:600;font-size:0.9rem;">시작</label>
		    <div class="col-sm-9 tui-datepicker-input tui-datetime-input tui-has-focus">
		      <input type="text" class="form-control" name="startDate" id="inputStartDate"/>
		      <span class="tui-ico-date"></span>
		    </div>
		    <div id="startWrapper" style="margin-top: -1px;"></div>
		  </div>
		  <div class="mb-3 row">
		    <label class="col-sm-3 col-form-label" style="font-weight:600;font-size:0.9rem;">종료</label>
		    <div class="col-sm-9 tui-datepicker-input tui-datetime-input tui-has-focus">
		      <input type="text" class="form-control" name="endDate" id="inputEndDate"/>
		      <span class="tui-ico-date"></span>
		    </div>
		    <div id="endWrapper" style="margin-top: -1px;"></div>
		  </div>
		  <div class="mb-3">
		    <label style="font-weight:600;font-size:0.9rem;">메모</label>
		    <textarea name="comments" class="form-control" placeholder="메모를 입력하세요" id="floatingTextarea2" rows="3"></textarea>
		  </div>
 	  </div>
      <div class="modal-footer" style="border-top:1px solid #e9ecef;">
        <button type="button" class="btn btn-board-danger" id="btnDelete" style="margin-right:auto;">삭제</button>
        <button type="button" class="btn btn-board-outline" data-dismiss="modal">닫기</button>
        <button id="btnSave" type="button" class="btn btn-board-primary">저장</button>
      </div>
    </div>
  </div>
</div>

<c:url var="listURL" value="/calendar/list"></c:url>
<c:url var="getListURL" value="/calendar/getList"></c:url>
<c:url var="deleteURL" value="/calendar/delete"></c:url>
<c:url var="saveURL" value="/calendar/save"></c:url>
<c:url var="syncURL" value="/calendar/google/sync"></c:url>

<script>
kangong.calendar = {
	save : function(){
		let scheduleObj = $("#createScheduleDiv").inputToObject();
		$.ajax({
			type:'post',
			url:'${saveURL}',
			contentType: "application/json",
			data: JSON.stringify(scheduleObj),
			success:function(result){
				kangong.form.submitPost("${listURL}", {});
			}
		});
	},
	delete : function(){
		if(!confirm('이 일정을 삭제하시겠습니까?')) return;
		let scheduleObj = $("#createScheduleDiv").inputToObject();
		if(!scheduleObj.id) { $('#createScheduleDiv').modal('hide'); return; }
		$.ajax({
			type:'post',
			url:'${deleteURL}',
			data: {id: scheduleObj.id},
			success:function(result){
				kangong.form.submitPost("${listURL}", {});
			}
		});
	},
	getList : function(){
		$.ajax({
			type:'post',
			url:'${getListURL}',
			contentType: "application/json",
			success:function(result){
				calendarObj.clear();
				calendarObj.createSchedules(result);
			}
		});
	},
	googleSync : function(){
		$('#syncResult').removeClass('success error').hide();
		$.ajax({
			type:'post',
			url:'${syncURL}',
			contentType: "application/json",
			success:function(result){
				if(result.success){
					$('#syncResult').addClass('success').text(result.message).show();
					kangong.calendar.getList();
				} else {
					$('#syncResult').addClass('error').text(result.message).show();
				}
				setTimeout(function(){ $('#syncResult').fadeOut(); }, 5000);
			},
			error:function(){
				$('#syncResult').addClass('error').text('동기화 중 오류가 발생했습니다.').show();
			}
		});
	},
	resetModal : function(){
		$('#createScheduleDiv').find(':input').each(function(){
			if(this.type !== 'hidden' && this.type !== 'button') {
				$(this).val('');
			}
		});
		$('#createScheduleDiv').find('input[name="id"]').val('');
		$('#createScheduleDiv').find('input[name="googleEventId"]').val('');
		$('#createScheduleDiv').find('input[name="category"]').val('time');
	}
};

var calendarObj;
$( document ).ready(function() {

	$('#btnSave').on('click', function(e){
		e.preventDefault();
		kangong.calendar.save();
	});

	$('#btnDelete').on('click', function(e){
		e.preventDefault();
		kangong.calendar.delete();
	});

	$('#btnGoogleSync').on('click', function(e){
		e.preventDefault();
		kangong.calendar.googleSync();
	});

	$('#btnCreateSchedule').on('click', function(){
		kangong.calendar.resetModal();
	});

	$('.toolbar-left').on('click', 'button', onClickNavi);

	var startDatepicker = new tui.DatePicker('#startWrapper', {
        date: new Date(),
        input: { element: '#inputStartDate', format: 'yyyy-MM-ddTHH:mm' },
        timePicker: true
    });

	var endDatepicker = new tui.DatePicker('#endWrapper', {
        date: new Date(),
        input: { element: '#inputEndDate', format: 'yyyy-MM-ddTHH:mm' },
        timePicker: true
    });

	calendarObj = new tui.Calendar(document.getElementById('calendarDiv'), {
	    defaultView: 'month',
	    taskView: true,
	    scheduleView: true,
	    useCreationPopup: false,
	    useDetailPopup: false,
	    timezones: [{
	        timezoneOffset: 540,
	        tooltip: 'Seoul'
	    }],
	    month: {
	        daynames: ['일', '월', '화', '수', '목', '금', '토'],
	        startDayOfWeek: 0,
	        narrowWeekend: true
	    },
	    week: {
	        daynames: ['일', '월', '화', '수', '목', '금', '토'],
	        startDayOfWeek: 0,
	        narrowWeekend: true
	    }
	});

    calendarObj.on({
        'clickSchedule': function(e) {
            kangong.calendar.resetModal();
            $("#createScheduleDiv").find(":input").each(function(index, item){
          	   if(!kangong.check.isNull( e.schedule[$(item).prop("name")] ) ){
          		 $(item).prop("value",e.schedule[$(item).prop("name")]);
          	   }
          	   if($(item).prop("name") == "startDate"){
          		 $(item).prop("value", e.schedule.start.toDate().toISOString());
          	   }
          	   if($(item).prop("name") == "endDate"){
          		 $(item).prop("value", e.schedule.end.toDate().toISOString());
          	   }
            });
            $('#createScheduleDiv').modal('show');
        },
        'beforeCreateSchedule': function(e) {
            kangong.calendar.resetModal();
            $('#createScheduleDiv').modal('show');
        },
        'beforeUpdateSchedule': function(e) {
            e.schedule.start = e.start;
            e.schedule.end = e.end;
            calendarObj.updateSchedule(e.schedule.id, e.schedule.calendarId, e.schedule);
        },
        'beforeDeleteSchedule': function(e) {
            calendarObj.deleteSchedule(e.schedule.id, e.schedule.calendarId);
        }
    });

	kangong.calendar.getList();
	setRenderRangeText();
});

function setRenderRangeText() {
    var options = calendarObj.getOptions();
    var viewName = calendarObj.getViewName();
    var html = [];
    if (viewName === 'day') {
        html.push(moment(calendarObj.getDate().getTime()).format('YYYY.MM.DD'));
    } else if (viewName === 'month') {
        html.push(moment(calendarObj.getDate().getTime()).format('YYYY.MM'));
    } else {
        html.push(moment(calendarObj.getDateRangeStart().getTime()).format('YYYY.MM.DD'));
        html.push(' ~ ');
        html.push(moment(calendarObj.getDateRangeEnd().getTime()).format('MM.DD'));
    }
    $('#renderRange').html(html.join(''));
}

function onClickNavi(e) {
	var action = getDataAction(e.target);
	switch (action) {
	    case 'move-prev': calendarObj.prev(); break;
	    case 'move-next': calendarObj.next(); break;
	    case 'move-today': calendarObj.today(); break;
	    default: return;
	}
	setRenderRangeText();
}

function getDataAction(target) {
	return target.dataset ? target.dataset.action : target.getAttribute('data-action');
}
</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.14.1/moment.min.js"></script>
