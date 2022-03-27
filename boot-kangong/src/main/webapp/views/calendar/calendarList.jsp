<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/views/include/include-header.jsp" %>


<link rel="stylesheet" type="text/css" href="https://uicdn.toast.com/tui-calendar/latest/tui-calendar.css" />

<!-- If you use the default popups, use this. -->
<link rel="stylesheet" type="text/css" href="https://uicdn.toast.com/tui.date-picker/latest/tui-date-picker.css" />
<link rel="stylesheet" type="text/css" href="https://uicdn.toast.com/tui.time-picker/latest/tui-time-picker.css" />
<link rel="stylesheet" type="text/css" href="https://nhn.github.io/tui.calendar/latest/examples/css/icons.css" />
<link rel="stylesheet" type="text/css" href="https://nhn.github.io/tui.calendar/latest/examples/css/default.css" />

<script src="https://uicdn.toast.com/tui.code-snippet/v1.5.2/tui-code-snippet.min.js"></script>
<script src="https://uicdn.toast.com/tui.time-picker/latest/tui-time-picker.min.js"></script>
<script src="https://uicdn.toast.com/tui.date-picker/latest/tui-date-picker.min.js"></script>
<script src="https://uicdn.toast.com/tui-calendar/latest/tui-calendar.js"></script>







<div id="menu">
  <span id="menu-navi">
    <button type="button" class="btn btn-sm move-today" data-action="move-today">Today</button>
    <button type="button" class="btn btn-sm move-day" data-action="move-prev">
      <i class="calendar-icon ic-arrow-line-left" data-action="move-prev"></i>
    </button>
    <button type="button" class="btn btn-sm move-day" data-action="move-next">
      <i class="calendar-icon ic-arrow-line-right" data-action="move-next"></i>
    </button>
  </span>
  <span id="renderRange" class="render-range"></span>
</div>


<div class="form-group row">
<div>
	<button class="btn btn-sm btn-primary" name="btnCreateSchedule" id="btnCreateSchedule" data-toggle="modal" data-target="#createSchedule">일정생성</button>
</div>
<div>
	<button class="btn btn-sm btn-primary" name="btnGetList" id="btnGetList" data-toggle="modal" >getList</button>
</div>
</div>

<div id="calendarDiv" style="height: 800px;"></div>







<div class="modal fade" id="createScheduleDiv" tabindex="-1" aria-labelledby="createSchedulelDivLabel" aria-hidden="true">>
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">일정 생성</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
          <input type="hidden"  name="id" />
          <input type="hidden"  name="category" value="time"/>
		  <div class="mb-3 row">
		    <label for="staticCategory" class="col-sm-2 col-form-label">Category</label>
		    <div class="col-sm-10">
		        <select class="form-select" name="calendarId" aria-label="Default select example">
				  <option selected>Open this select category</option>
				  <option value="1">Family</option>
				  <option value="2">Work</option>
				  <option value="3">Friends</option>
				  <option value="4">School</option>
				  <option value="5">Etc</option>
				</select>
		    </div>
		  </div>
		  <div class="mb-3 row">
		    <label for="inputTitle" class="col-sm-2 col-form-label">Title</label>
		    <div class="col-sm-10">
		      <input type="text" class="form-control" name="title" id="inputTitle">
		    </div>
		  </div>
		  <div class="mb-3 row">
		    <label for="inputTitle" class="col-sm-3 col-form-label">일정</label>
		    <div class="col-sm-4 tui-datepicker-input tui-datetime-input tui-has-focus">
		      <input type="text" class="form-control" name="startDate" id="inputStartDate"/>
		      <span class="tui-ico-date"></span>
		    </div>
		    <div id="startWrapper" style="margin-top: -1px;"></div>
		    
		    <div class="col-sm-4  tui-datepicker-input tui-datetime-input tui-has-focus">
		      <input type="text" class="form-control" name="endDate" id="inputEndDate"/>
		      <span class="tui-ico-date"></span>      
		    </div>
		    <div id="endWrapper" style="margin-top: -1px;"></div>
		  </div>
		  
		  
		  <div class="form-floating">
		  <label for="floatingTextarea2">Comments</label>
		  <textarea name="comments" class="form-control" placeholder="Leave a comment here" id="floatingTextarea2" style="height: 100px"></textarea>  
		</div>


 	   </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
        <button id="btnSave" type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>

<c:url var="listURL" value="/calendar/list"></c:url>
<c:url var="getListURL" value="/calendar/getList"></c:url>
<c:url var="deleteURL" value="/calendar/delete"></c:url>
<c:url var="saveURL" value="/calendar/save"></c:url>

<!-- https://bleepcoder.com/ko/tui-calendar/322219553/customize-popup -->
<script>

kangong.calendar = {
		save : function(){
			let scheduleObj = $("#createScheduleDiv").inputToObject();
			console.log("scheduleObj:"+JSON.stringify(scheduleObj));
			$.ajax(
				    {
				        type:'post',
				        url:'${saveURL}',
				        contentType: "application/json",
				        data: JSON.stringify(scheduleObj),
				        success:function(result)
				        {
				             kangong.form.submitPost("${listURL}", {});
				        }
				    });
		},
		delete : function(){
			let scheduleObj = $("#createScheduleDiv").inputToObject();
			console.log("scheduleObj:"+JSON.stringify(scheduleObj));
			$.ajax(
				    {
				        type:'post',
				        url:'${deleteURL}',
				        contentType: "application/json",
				        data: JSON.stringify(scheduleObj),
				        success:function(result)
				        {
				             kangong.form.submitPost("${listURL}", {});
				        }
				    });
		},
		getList :  function(){
			$.ajax(
				    {
				        type:'post',
				        url:'${getListURL}',
				        contentType: "application/json",
				        //data: JSON.stringify(scheduleObj),
				        success:function(result)
				        {
				             console.log("result", result);
				             calendarObj.clear();
				             calendarObj.createSchedules(result);
				        }
				    });
		}
	};

var calendarObj;
$( document ).ready(function() {
	
	$('#createScheduleDiv').on('show.bs.modal', function (event) {
		$('#createScheduleDiv').modal('show');
		  var button = $(event.relatedTarget) // Button that triggered the modal
		  
	});
	
	$('#btnSave').on('click', function(e){
		console.log("btnSave");
		e.preventDefault();
		kangong.calendar.save();
	});
	
	$('#btnGetList').on('click', function(e){
		console.log("btnGetList");
		e.preventDefault();
		kangong.calendar.getList();
	});
	
	$('#menu-navi').on('click', onClickNavi);
	
	var startDatepicker = new tui.DatePicker('#startWrapper', {
        date: new Date(),
        input: {
            element: '#inputStartDate',
            format: 'yyyy-MM-ddTHH:mm' //'yyyy-MM-dd HH:mm A'
        },
        timePicker: true
    });
	
	var endDatepicker = new tui.DatePicker('#endWrapper', {
        date: new Date(),
        input: {
            element: '#inputEndDate',
            format: 'yyyy-MM-ddTHH:mm'//'yyyy-MM-dd HH:mm A'
        },
        timePicker: true
    });
	
	calendarObj = new tui.Calendar(document.getElementById('calendarDiv'), {
	    defaultView: 'month',
	    taskView: true,    // Can be also ['milestone', 'task']
	    scheduleView: true,  // Can be also ['allday', 'time']
	    useCreationPopup: false,
	    useDetailPopup: false,
	    timezones: [{
	        timezoneOffset: 540,
	        // displayLabel: 'GMT+09:00',
	        tooltip: 'Seoul'
	    }, {
	        timezoneOffset: -420,
	        // displayLabel: 'GMT-08:00',
	        tooltip: 'Los Angeles'
	    }],
	   // template: templates,
	    month: {
	        daynames: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
	        startDayOfWeek: 0,
	        narrowWeekend: true
	    },
	    week: {
	        daynames: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
	        startDayOfWeek: 0,
	        narrowWeekend: true
	    }
	});
	
	
	
	// event handlers
    calendarObj.on({
        'clickSchedule': function(e) {
            console.log('clickSchedule', e);
            
            $("#createScheduleDiv").find(":input").each(function(index, item){
          	  console.log("item",$(item).prop("name"), e.schedule[$(item).prop("name")]);
          	   if(!kangong.check.isNull( e.schedule[$(item).prop("name")] ) ){
          		 $(item).prop("value",e.schedule[$(item).prop("name")]);
                 console.log("value", $(item).prop("value"));
          	   }
          	   
          	   if($(item).prop("name") == "startDate"){
          		   console.log("startDate", e.schedule.start, e.schedule.start.toDate().toISOString() );
          		 $(item).prop("value", e.schedule.start.toDate().toISOString());
          	   }
          	   
          	 if($(item).prop("name") == "endDate"){
        		   console.log("endDate", e.schedule.start, e.schedule.end.toDate().toISOString() );
        		 $(item).prop("value", e.schedule.end.toDate().toISOString());
        	   }
          	  
             });
            
            $('#createScheduleDiv').modal('show');
            /* step1. open custom detail popup 
            const willModify = confirm(`title: ${e.schedule.title}\n Will you update schedule?`);

            if (willModify) { // step1-1. open edit popup 
                e.schedule.title = prompt('Schedule', e.schedule.title);
                calendarObj.updateSchedule(e.schedule.id, e.schedule.calendarId, e.schedule);
            }
            */
        },
        'beforeCreateSchedule': function(e) {
            console.log('beforeCreateSchedule', e);
            /* step1. open custom edit popup
            const title = prompt('Schedule', '@suvrity\'s birthday');
            var schedule = {
                id: +new Date(),
                title: title,
                isAllDay: true,
                start: e.start,
                end: e.end,
                category:  'allday'
            };
            */
            /* step2. save schedule */
            //calendarObj.createSchedules([schedule]);
            /* step3. clear guide element */
           // e.guide.clearGuideElement();
            
            $('#createScheduleDiv').modal('show');
        },
        'beforeUpdateSchedule': function(e) {
            console.log('beforeUpdateSchedule', e);
            
          
            
            
            e.schedule.start = e.start;
            e.schedule.end = e.end;
            calendarObj.updateSchedule(e.schedule.id, e.schedule.calendarId, e.schedule);
        },
        'beforeDeleteSchedule': function(e) {
            console.log('beforeDeleteSchedule', e);
            calendarObj.deleteSchedule(e.schedule.id, e.schedule.calendarId);
        }
    });

	
	
	
	  function getTimeTemplate(schedule, isAllDay) {
		    var html = [];

		    if (!isAllDay) {
		      html.push('<strong>' + moment(schedule.start.getTime()).format('HH:mm') + '</strong> ');
		    }
		    if (schedule.isPrivate) {
		      html.push('<span class="calendar-font-icon ic-lock-b"></span>');
		      html.push(' Private');
		    } else {
		      if (schedule.isReadOnly) {
		        html.push('<span class="calendar-font-icon ic-readonly-b"></span>');
		      } else if (schedule.recurrenceRule) {
		        html.push('<span class="calendar-font-icon ic-repeat-b"></span>');
		      } else if (schedule.attendees.length) {
		        html.push('<span class="calendar-font-icon ic-user-b"></span>');
		      } else if (schedule.location) {
		        html.push('<span class="calendar-font-icon ic-location-b"></span>');
		      }
		      html.push(' ' + schedule.title);
		    }

		    return html.join('');
		  }

		  function getGridTitleTemplate(type) {
		    var title = '';

		    switch(type) {
		      case 'milestone':
		        title = '<span class="tui-full-calendar-left-content">MILESTONE</span>';
		        break;
		      case 'task':
		        title = '<span class="tui-full-calendar-left-content">TASK</span>';
		        break;
		      case 'allday':
		        title = '<span class="tui-full-calendar-left-content">ALL DAY</span>';
		        break;
		    }

		    return title;
		  }

		  function getGridCategoryTemplate(category, schedule) {
		    var tpl;

		    switch(category) {
		      case 'milestone':
		        tpl = '<span class="calendar-font-icon ic-milestone-b"></span> <span style="background-color: ' + schedule.bgColor + '">' + schedule.title + '</span>';
		        break;
		      case 'task':
		        tpl = '#' + schedule.title;
		        break;
		      case 'allday':
		        tpl = getTimeTemplate(schedule, true);
		        break;
		    }

		    return tpl;
		  }

		  // register templates
		  var templates = {
		    milestone: function(schedule) {
		      return getGridCategoryTemplate('milestone', schedule);
		    },
		    milestoneTitle: function() {
		      return getGridTitleTemplate('milestone');
		    },
		    task: function(schedule) {
		      return getGridCategoryTemplate('task', schedule);
		    },
		    taskTitle: function() {
		      return getGridTitleTemplate('task');
		    },
		    allday: function(schedule) {
		      return getTimeTemplate(schedule, true);
		    },
		    alldayTitle: function() {
		      return getGridTitleTemplate('allday');
		    },
		    time: function(schedule) {
		      return getTimeTemplate(schedule, false);
		    },
		    goingDuration: function(schedule) {
		      return '<span class="calendar-icon ic-travel-time"></span>' + schedule.goingDuration + 'min.';
		    },
		    comingDuration: function(schedule) {
		      return '<span class="calendar-icon ic-travel-time"></span>' + schedule.comingDuration + 'min.';
		    },
		    monthMoreTitleDate: function(date, dayname) {
		      var day = date.split('.')[2];
		      return '<span class="tui-full-calendar-month-more-title-day">' + day + '</span> <span class="tui-full-calendar-month-more-title-day-label">' + dayname + '</span>';
		    },
		    monthMoreClose: function() {
		      return '<span class="tui-full-calendar-icon tui-full-calendar-ic-close"></span>';
		    },
		    monthGridHeader: function(dayModel) {
		      var date = parseInt(dayModel.date.split('-')[2], 10);
		      var classNames = ['tui-full-calendar-weekday-grid-date '];

		      if (dayModel.isToday) {
		        classNames.push('tui-full-calendar-weekday-grid-date-decorator');
		      }

		      return '<span class="' + classNames.join(' ') + '">' + date + '</span>';
		    },
		    monthGridHeaderExceed: function(hiddenSchedules) {
		      return '<span class="weekday-grid-more-schedules">+' + hiddenSchedules + '</span>';
		    },
		    monthGridFooter: function() {
		      return '';
		    },
		    monthGridFooterExceed: function(hiddenSchedules) {
		      return '';
		    },
		    monthDayname: function(model) {
		      return String(model.label).toLocaleUpperCase();
		    },
		    dayGridTitle: function(viewName) {
		      /*
		       * use another functions instead of 'dayGridTitle'
		       * milestoneTitle: function() {...}
		       * taskTitle: function() {...}
		       * alldayTitle: function() {...}
		      */

		      return getGridTitleTemplate(viewName);
		    },
		    schedule: function(schedule) {
		      /*
		       * use another functions instead of 'schedule'
		       * milestone: function() {...}
		       * task: function() {...}
		       * allday: function() {...}
		      */

		      return getGridCategoryTemplate(schedule.category, schedule);
		    }
		  };
	

	
	calendarObj.createSchedules([
	    {
	        id: '1',
	        calendarId: '1',
	        title: 'my schedule',
	        category: 'time',
	        dueDateClass: '',
	        start: '2021-06-11T22:30:00+09:00',
	        end: '2021-06-12T02:30:00+09:00'
	    },
	    {
	        id: '2',
	        calendarId: '2',
	        title: 'second schedule',
	        category: 'time',
	        dueDateClass: '',
	        start: '2021-06-04T17:30:00+09:00',
	        end: '2021-06-06T17:31:00+09:00'
	    }
	]);
});

function yyyymmdd(dateIn) {
    var yyyy = dateIn.getFullYear();
    var mm = dateIn.getMonth()+1; // getMonth() is zero-based
    var dd  = dateIn.getDate();
    return String(10000*yyyy + 100*mm + dd); // Leading zeros for mm and dd
}

function onClickNavi(e) {
	  var action = getDataAction(e.target);

	  switch (action) {
	    case 'move-prev':
	      calendarObj.prev();
	      break;
	    case 'move-next':
	      calendarObj.next();
	      break;
	    case 'move-today':
	      calendarObj.today();
	      break;
	    default:
	      return;
	  }

	  //setRenderRangeText();
	  //setSchedules();
 }

function getDataAction(target) {
	  return target.dataset ? target.dataset.action : target.getAttribute('data-action');
 }
	
</script>