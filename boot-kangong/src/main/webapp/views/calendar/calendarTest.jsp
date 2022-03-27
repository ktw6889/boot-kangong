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

<!--  
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.2/moment-with-locales.min.js"></script>
<script src="${pageContext.request.contextPath}/lib/bootstrap-datepicker/dist/js/bootstrap-datepicker.js"></script>
<script src="${pageContext.request.contextPath}/lib/bootstrap-datepicker/dist/locales/bootstrap-datepicker.ko.min.js"></script>
-->

<button type="button" id="modalButton" class="btn btn-primary" data-toggle="modal" data-target="#createSchedule">
  Launch static backdrop modal
</button>

/**
1. id
2. calendarId
3. category
4. title
5. start
6. end
7. desc
 */
<div class="modal fade" id="createSchedule" tabindex="-1" aria-labelledby="createSchedulelLabel" aria-hidden="true">>
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Modal title</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      
		  <div class="mb-3 row">
		    <label for="staticCategory" class="col-sm-2 col-form-label">Category</label>
		    <div class="col-sm-10">
		        <select class="form-select" aria-label="Default select example">
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
		    <label for="inputTitle" class="col-sm-2 col-form-label">일정</label>
		    <div class="col-sm-5 tui-datepicker-input tui-datetime-input tui-has-focus">
		      <input type="text" class="form-control" name="startDate" id="inputStartDate"/>
		      <span class="tui-ico-date"></span>
		    </div>
		    <div id="wrapper" style="margin-top: -1px;"></div>
		    
		    <div class="col-sm-5  tui-datepicker-input tui-datetime-input tui-has-focus"">
		      <input type="text" class="form-control" name="endDate" id="inputEndDate"/>
		      <span class="tui-ico-date"></span>      
		    </div>
		  </div>
		  
		  
		  <div class="form-floating">
		  <label for="floatingTextarea2">Comments</label>
		  <textarea class="form-control" placeholder="Leave a comment here" id="floatingTextarea2" style="height: 100px"></textarea>  
		</div>


 	   </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Save changes</button>
      </div>
    </div>
  </div>
</div>






<div class="tui-full-calendar-popup" style="display:none;">
    <div class="tui-full-calendar-popup-container">
        <div class="tui-full-calendar-popup-section tui-full-calendar-dropdown tui-full-calendar-close tui-full-calendar-section-calendar">
            <button class="tui-full-calendar-button tui-full-calendar-dropdown-button tui-full-calendar-popup-section-item">
                <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #9e5fff"></span>
                <span id="tui-full-calendar-schedule-calendar" class="tui-full-calendar-content">My Calendar</span>
                <span class="tui-full-calendar-icon tui-full-calendar-dropdown-arrow"></span>
            </button>
            <ul class="tui-full-calendar-dropdown-menu" style="z-index: 1015">
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="1">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #9e5fff"></span>
                        <span class="tui-full-calendar-content">My Calendar</span>
                    </li>
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="2">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #00a9ff"></span>
                        <span class="tui-full-calendar-content">Company</span>
                    </li>
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="3">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #ff5583"></span>
                        <span class="tui-full-calendar-content">Family</span>
                    </li>
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="4">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #03bd9e"></span>
                        <span class="tui-full-calendar-content">Friend</span>
                    </li>
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="5">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #bbdc00"></span>
                        <span class="tui-full-calendar-content">Travel</span>
                    </li>
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="6">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #9d9d9d"></span>
                        <span class="tui-full-calendar-content">etc</span>
                    </li>
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="7">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #ffbb3b"></span>
                        <span class="tui-full-calendar-content">Birthdays</span>
                    </li>
                    <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item" data-calendar-id="8">
                        <span class="tui-full-calendar-icon tui-full-calendar-calendar-dot" style="background-color: #ff4040"></span>
                        <span class="tui-full-calendar-content">National Holidays</span>
                    </li>
            </ul>
        </div>
        <div class="tui-full-calendar-popup-section">
            <div class="tui-full-calendar-popup-section-item tui-full-calendar-section-title">
            <span class="tui-full-calendar-icon tui-full-calendar-ic-title"></span>
                <input id="tui-full-calendar-schedule-title" class="tui-full-calendar-content" placeholder="Subject" value="">
            </div>
            <button id="tui-full-calendar-schedule-private" class="tui-full-calendar-button tui-full-calendar-section-private tui-full-calendar-public">
            <span class="tui-full-calendar-icon tui-full-calendar-ic-private"></span>
            </button>
        </div>
        <div class="tui-full-calendar-popup-section">
            <div class="tui-full-calendar-popup-section-item tui-full-calendar-section-location">
            <span class="tui-full-calendar-icon tui-full-calendar-ic-location"></span>
                <input id="tui-full-calendar-schedule-location" class="tui-full-calendar-content" placeholder="Location" value="">
            </div>
        </div>
        <div class="tui-full-calendar-popup-section">
            <div class="tui-full-calendar-popup-section-item tui-full-calendar-section-start-date">
                <span class="tui-full-calendar-icon tui-full-calendar-ic-date"></span>
                <input id="tui-full-calendar-schedule-start-date" class="tui-full-calendar-content" placeholder="Start date">
                <div id="tui-full-calendar-startpicker-container" style="margin-left: -1px; position: relative"><div class="tui-datepicker tui-hidden tui-rangepicker">            <div class="tui-datepicker-body tui-datepicker-type-date">        <div class="tui-calendar-container"><div class="tui-calendar">    <div class="tui-calendar-header"><div class="tui-calendar-header-inner">      <button class="tui-calendar-btn tui-calendar-btn-prev-month">Prev month</button>      <em class="tui-calendar-title tui-calendar-title-month">June 2021</em>      <button class="tui-calendar-btn tui-calendar-btn-next-month">Next month</button>    </div>    <div class="tui-calendar-header-info">    <p class="tui-calendar-title-today">Today: Saturday, June 12, 2021</p>  </div></div>    <div class="tui-calendar-body"><table class="tui-calendar-body-inner" cellspacing="0" cellpadding="0">  <caption><span>Dates</span></caption>  <thead class="tui-calendar-body-header">    <tr>      <th class="tui-sun" scope="col">Sun</th>      <th scope="col">Mon</th>      <th scope="col">Tue</th>      <th scope="col">Wed</th>      <th scope="col">Thu</th>      <th scope="col">Fri</th>      <th class="tui-sat" scope="col">Sat</th>    </tr>  </thead>  <tbody>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-prev-month tui-calendar-sun tui-is-selectable" data-timestamp="1622300400000">30</td>            <td class="tui-calendar-date tui-calendar-prev-month tui-is-selectable" data-timestamp="1622386800000">31</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1622473200000">1</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1622559600000">2</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1622646000000">3</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1622732400000">4</td>            <td class="tui-calendar-date tui-calendar-sat tui-is-selectable" data-timestamp="1622818800000">5</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-selectable" data-timestamp="1622905200000">6</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1622991600000">7</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623078000000">8</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623164400000">9</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623250800000">10</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623337200000">11</td>            <td class="tui-calendar-date tui-calendar-sat tui-calendar-today tui-is-selectable tui-is-selected" data-timestamp="1623423600000">12</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-selectable" data-timestamp="1623510000000">13</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623596400000">14</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623682800000">15</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623769200000">16</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623855600000">17</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623942000000">18</td>            <td class="tui-calendar-date tui-calendar-sat tui-is-selectable" data-timestamp="1624028400000">19</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-selectable" data-timestamp="1624114800000">20</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624201200000">21</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624287600000">22</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624374000000">23</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624460400000">24</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624546800000">25</td>            <td class="tui-calendar-date tui-calendar-sat tui-is-selectable" data-timestamp="1624633200000">26</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-selectable" data-timestamp="1624719600000">27</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624806000000">28</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624892400000">29</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624978800000">30</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625065200000">1</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625151600000">2</td>            <td class="tui-calendar-date tui-calendar-next-month tui-calendar-sat tui-is-selectable" data-timestamp="1625238000000">3</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-next-month tui-calendar-sun tui-is-selectable" data-timestamp="1625324400000">4</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625410800000">5</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625497200000">6</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625583600000">7</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625670000000">8</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625756400000">9</td>            <td class="tui-calendar-date tui-calendar-next-month tui-calendar-sat tui-is-selectable" data-timestamp="1625842800000">10</td>          </tr>      </tbody></table></div></div></div>      </div>      <div class="tui-datepicker-footer">        <div class="tui-timepicker-container"><div class="tui-timepicker">  <div class="tui-timepicker-body">    <div class="tui-timepicker-row">              <div class="tui-timepicker-column tui-timepicker-selectbox tui-timepicker-hour"><select class="tui-timepicker-select" aria-label="Time">            <option value="0" selected="">0</option>                <option value="1">1</option>                <option value="2">2</option>                <option value="3">3</option>                <option value="4">4</option>                <option value="5">5</option>                <option value="6">6</option>                <option value="7">7</option>                <option value="8">8</option>                <option value="9">9</option>                <option value="10">10</option>                <option value="11">11</option>                <option value="12">12</option>                <option value="13">13</option>                <option value="14">14</option>                <option value="15">15</option>                <option value="16">16</option>                <option value="17">17</option>                <option value="18">18</option>                <option value="19">19</option>                <option value="20">20</option>                <option value="21">21</option>                <option value="22">22</option>                <option value="23">23</option>      </select></div>        <span class="tui-timepicker-column tui-timepicker-colon"><span class="tui-ico-colon">:</span></span>        <div class="tui-timepicker-column tui-timepicker-selectbox tui-timepicker-minute"><select class="tui-timepicker-select" aria-label="Time">            <option value="0" selected="">0</option>                <option value="1">1</option>                <option value="2">2</option>                <option value="3">3</option>                <option value="4">4</option>                <option value="5">5</option>                <option value="6">6</option>                <option value="7">7</option>                <option value="8">8</option>                <option value="9">9</option>                <option value="10">10</option>                <option value="11">11</option>                <option value="12">12</option>                <option value="13">13</option>                <option value="14">14</option>                <option value="15">15</option>                <option value="16">16</option>                <option value="17">17</option>                <option value="18">18</option>                <option value="19">19</option>                <option value="20">20</option>                <option value="21">21</option>                <option value="22">22</option>                <option value="23">23</option>                <option value="24">24</option>                <option value="25">25</option>                <option value="26">26</option>                <option value="27">27</option>                <option value="28">28</option>                <option value="29">29</option>                <option value="30">30</option>                <option value="31">31</option>                <option value="32">32</option>                <option value="33">33</option>                <option value="34">34</option>                <option value="35">35</option>                <option value="36">36</option>                <option value="37">37</option>                <option value="38">38</option>                <option value="39">39</option>                <option value="40">40</option>                <option value="41">41</option>                <option value="42">42</option>                <option value="43">43</option>                <option value="44">44</option>                <option value="45">45</option>                <option value="46">46</option>                <option value="47">47</option>                <option value="48">48</option>                <option value="49">49</option>                <option value="50">50</option>                <option value="51">51</option>                <option value="52">52</option>                <option value="53">53</option>                <option value="54">54</option>                <option value="55">55</option>                <option value="56">56</option>                <option value="57">57</option>                <option value="58">58</option>                <option value="59">59</option>      </select></div>                  </div>  </div></div></div>      </div>      </div></div>
            </div>
            <span class="tui-full-calendar-section-date-dash">-</span>
            <div class="tui-full-calendar-popup-section-item tui-full-calendar-section-end-date">
                <span class="tui-full-calendar-icon tui-full-calendar-ic-date"></span>
                <input id="tui-full-calendar-schedule-end-date" class="tui-full-calendar-content" placeholder="End date">
                <div id="tui-full-calendar-endpicker-container" style="margin-left: -1px; position: relative"><div class="tui-datepicker tui-hidden tui-rangepicker">            <div class="tui-datepicker-body tui-datepicker-type-date">        <div class="tui-calendar-container"><div class="tui-calendar">    <div class="tui-calendar-header"><div class="tui-calendar-header-inner">      <button class="tui-calendar-btn tui-calendar-btn-prev-month tui-hidden">Prev month</button>      <em class="tui-calendar-title tui-calendar-title-month">June 2021</em>      <button class="tui-calendar-btn tui-calendar-btn-next-month">Next month</button>    </div>    <div class="tui-calendar-header-info">    <p class="tui-calendar-title-today">Today: Saturday, June 12, 2021</p>  </div></div>    <div class="tui-calendar-body"><table class="tui-calendar-body-inner" cellspacing="0" cellpadding="0">  <caption><span>Dates</span></caption>  <thead class="tui-calendar-body-header">    <tr>      <th class="tui-sun" scope="col">Sun</th>      <th scope="col">Mon</th>      <th scope="col">Tue</th>      <th scope="col">Wed</th>      <th scope="col">Thu</th>      <th scope="col">Fri</th>      <th class="tui-sat" scope="col">Sat</th>    </tr>  </thead>  <tbody>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-prev-month tui-calendar-sun tui-is-blocked" data-timestamp="1622300400000">30</td>            <td class="tui-calendar-date tui-calendar-prev-month tui-is-blocked" data-timestamp="1622386800000">31</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1622473200000">1</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1622559600000">2</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1622646000000">3</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1622732400000">4</td>            <td class="tui-calendar-date tui-calendar-sat tui-is-blocked" data-timestamp="1622818800000">5</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-blocked" data-timestamp="1622905200000">6</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1622991600000">7</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1623078000000">8</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1623164400000">9</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1623250800000">10</td>            <td class="tui-calendar-date tui-is-blocked" data-timestamp="1623337200000">11</td>            <td class="tui-calendar-date tui-calendar-sat tui-calendar-today tui-is-selectable tui-is-selected tui-is-selected-range" data-timestamp="1623423600000">12</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-selectable" data-timestamp="1623510000000">13</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623596400000">14</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623682800000">15</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623769200000">16</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623855600000">17</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1623942000000">18</td>            <td class="tui-calendar-date tui-calendar-sat tui-is-selectable" data-timestamp="1624028400000">19</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-selectable" data-timestamp="1624114800000">20</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624201200000">21</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624287600000">22</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624374000000">23</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624460400000">24</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624546800000">25</td>            <td class="tui-calendar-date tui-calendar-sat tui-is-selectable" data-timestamp="1624633200000">26</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-sun tui-is-selectable" data-timestamp="1624719600000">27</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624806000000">28</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624892400000">29</td>            <td class="tui-calendar-date tui-is-selectable" data-timestamp="1624978800000">30</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625065200000">1</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625151600000">2</td>            <td class="tui-calendar-date tui-calendar-next-month tui-calendar-sat tui-is-selectable" data-timestamp="1625238000000">3</td>          </tr>        <tr class="tui-calendar-week">            <td class="tui-calendar-date tui-calendar-next-month tui-calendar-sun tui-is-selectable" data-timestamp="1625324400000">4</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625410800000">5</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625497200000">6</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625583600000">7</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625670000000">8</td>            <td class="tui-calendar-date tui-calendar-next-month tui-is-selectable" data-timestamp="1625756400000">9</td>            <td class="tui-calendar-date tui-calendar-next-month tui-calendar-sat tui-is-selectable" data-timestamp="1625842800000">10</td>          </tr>      </tbody></table></div></div></div>      </div>      <div class="tui-datepicker-footer">        <div class="tui-timepicker-container"><div class="tui-timepicker">  <div class="tui-timepicker-body">    <div class="tui-timepicker-row">              <div class="tui-timepicker-column tui-timepicker-selectbox tui-timepicker-hour"><select class="tui-timepicker-select" aria-label="Time">            <option value="0" selected="">0</option>                <option value="1">1</option>                <option value="2">2</option>                <option value="3">3</option>                <option value="4">4</option>                <option value="5">5</option>                <option value="6">6</option>                <option value="7">7</option>                <option value="8">8</option>                <option value="9">9</option>                <option value="10">10</option>                <option value="11">11</option>                <option value="12">12</option>                <option value="13">13</option>                <option value="14">14</option>                <option value="15">15</option>                <option value="16">16</option>                <option value="17">17</option>                <option value="18">18</option>                <option value="19">19</option>                <option value="20">20</option>                <option value="21">21</option>                <option value="22">22</option>                <option value="23">23</option>      </select></div>        <span class="tui-timepicker-column tui-timepicker-colon"><span class="tui-ico-colon">:</span></span>        <div class="tui-timepicker-column tui-timepicker-selectbox tui-timepicker-minute"><select class="tui-timepicker-select" aria-label="Time">            <option value="0" selected="">0</option>                <option value="1">1</option>                <option value="2">2</option>                <option value="3">3</option>                <option value="4">4</option>                <option value="5">5</option>                <option value="6">6</option>                <option value="7">7</option>                <option value="8">8</option>                <option value="9">9</option>                <option value="10">10</option>                <option value="11">11</option>                <option value="12">12</option>                <option value="13">13</option>                <option value="14">14</option>                <option value="15">15</option>                <option value="16">16</option>                <option value="17">17</option>                <option value="18">18</option>                <option value="19">19</option>                <option value="20">20</option>                <option value="21">21</option>                <option value="22">22</option>                <option value="23">23</option>                <option value="24">24</option>                <option value="25">25</option>                <option value="26">26</option>                <option value="27">27</option>                <option value="28">28</option>                <option value="29">29</option>                <option value="30">30</option>                <option value="31">31</option>                <option value="32">32</option>                <option value="33">33</option>                <option value="34">34</option>                <option value="35">35</option>                <option value="36">36</option>                <option value="37">37</option>                <option value="38">38</option>                <option value="39">39</option>                <option value="40">40</option>                <option value="41">41</option>                <option value="42">42</option>                <option value="43">43</option>                <option value="44">44</option>                <option value="45">45</option>                <option value="46">46</option>                <option value="47">47</option>                <option value="48">48</option>                <option value="49">49</option>                <option value="50">50</option>                <option value="51">51</option>                <option value="52">52</option>                <option value="53">53</option>                <option value="54">54</option>                <option value="55">55</option>                <option value="56">56</option>                <option value="57">57</option>                <option value="58">58</option>                <option value="59">59</option>      </select></div>                  </div>  </div></div></div>      </div>      </div></div>
            </div>
            <div class="tui-full-calendar-popup-section-item tui-full-calendar-section-allday">
                <input id="tui-full-calendar-schedule-allday" type="checkbox" class="tui-full-calendar-checkbox-square">
                <span class="tui-full-calendar-icon tui-full-calendar-ic-checkbox"></span>
                <span class="tui-full-calendar-content">All day</span>
            </div>
        </div>
        <div class="tui-full-calendar-popup-section tui-full-calendar-dropdown tui-full-calendar-close tui-full-calendar-section-state">
            <button class="tui-full-calendar-button tui-full-calendar-dropdown-button tui-full-calendar-popup-section-item">
                <span class="tui-full-calendar-icon tui-full-calendar-ic-state"></span>
                <span id="tui-full-calendar-schedule-state" class="tui-full-calendar-content">Busy</span>
                <span class="tui-full-calendar-icon tui-full-calendar-dropdown-arrow"></span>
            </button>
            <ul class="tui-full-calendar-dropdown-menu" style="z-index: 1015">
                <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item">
                <span class="tui-full-calendar-icon tui-full-calendar-none"></span>
                <span class="tui-full-calendar-content">Busy</span>
                </li>
                <li class="tui-full-calendar-popup-section-item tui-full-calendar-dropdown-menu-item">
                <span class="tui-full-calendar-icon tui-full-calendar-none"></span>
                <span class="tui-full-calendar-content">Free</span>
                </li>
            </ul>
        </div>
        <button class="tui-full-calendar-button tui-full-calendar-popup-close"><span class="tui-full-calendar-icon tui-full-calendar-ic-close"></span></button>
        <div class="tui-full-calendar-section-button-save"><button class="tui-full-calendar-button tui-full-calendar-confirm tui-full-calendar-popup-save"><span>Save</span></button></div>
    </div>
    <div id="tui-full-calendar-popup-arrow" class="tui-full-calendar-popup-arrow tui-full-calendar-arrow-bottom">
        <div class="tui-full-calendar-popup-arrow-border" style="left: 412.219px;">
            <div class="tui-full-calendar-popup-arrow-fill"></div>
        </div>
    </div>
</div>




<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal" data-whatever="@mdo">Open modal for @mdo</button>
<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal" data-whatever="@fat">Open modal for @fat</button>
<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal" data-whatever="@getbootstrap">Open modal for @getbootstrap</button>

<div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">New message</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <form>
          <div class="form-group">
            <label for="recipient-name" class="col-form-label">Recipient:</label>
            <input type="text" class="form-control" id="recipient-name">
          </div>
          <div class="form-group">
            <label for="message-text" class="col-form-label">Message:</label>
            <textarea class="form-control" id="message-text"></textarea>
          </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary">Send message</button>
      </div>
    </div>
  </div>
</div>



<div id="calendarDiv" style="height: 800px;"></div>


<script>
$( document ).ready(function() {
	
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
	
	var datepicker = new tui.DatePicker('#wrapper', {
           date: new Date(),
           input: {
               element: '#inputStartDate',
               format: 'yyyy-MM-dd HH:mm A'
           },
           timePicker: true
       });
	
	
	$('#exampleModal').on('show.bs.modal', function (event) {
		  var button = $(event.relatedTarget) // Button that triggered the modal
		  var recipient = button.data('whatever') // Extract info from data-* attributes
		  // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
		  // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
		  var modal = $(this)
		  modal.find('.modal-title').text('New message to ' + recipient)
		  modal.find('.modal-body input').val(recipient)
		})
		
	$('#createSchedule').on('show.bs.modal', function (event) {
		$('#createSchedule').modal('show');
		  var button = $(event.relatedTarget) // Button that triggered the modal
		  var recipient = button.data('whatever') // Extract info from data-* attributes
		  // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
		  // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
		  var modal = $(this)
		  modal.find('.modal-title').text('New message to ' + recipient)
		  modal.find('.modal-body input').val(recipient)
		})	
		
	/*
	$('#inputStartDate2').datepicker({
		format: "yyyy-mm-dd",	//데이터 포맷 형식(yyyy : 년 mm : 월 dd : 일 )
	    startDate: '-10d',	//달력에서 선택 할 수 있는 가장 빠른 날짜. 이전으로는 선택 불가능 ( d : 일 m : 달 y : 년 w : 주)
	    endDate: '+10d',	//달력에서 선택 할 수 있는 가장 느린 날짜. 이후로 선택 불가 ( d : 일 m : 달 y : 년 w : 주)
	    autoclose : true,	//사용자가 날짜를 클릭하면 자동 캘린더가 닫히는 옵션
	    calendarWeeks : false, //캘린더 옆에 몇 주차인지 보여주는 옵션 기본값 false 보여주려면 true
	    clearBtn : false, //날짜 선택한 값 초기화 해주는 버튼 보여주는 옵션 기본값 false 보여주려면 true
	    datesDisabled : ['2019-06-24','2019-06-26'],//선택 불가능한 일 설정 하는 배열 위에 있는 format 과 형식이 같아야함.
	    daysOfWeekDisabled : [0,6],	//선택 불가능한 요일 설정 0 : 일요일 ~ 6 : 토요일
	    daysOfWeekHighlighted : [3], //강조 되어야 하는 요일 설정
	    disableTouchKeyboard : false,	//모바일에서 플러그인 작동 여부 기본값 false 가 작동 true가 작동 안함.
	    immediateUpdates: false,	//사용자가 보는 화면으로 바로바로 날짜를 변경할지 여부 기본값 :false 
	    multidate : false, //여러 날짜 선택할 수 있게 하는 옵션 기본값 :false 
	    multidateSeparator :",", //여러 날짜를 선택했을 때 사이에 나타나는 글짜 2019-05-01,2019-06-01
	    templates : {
	        leftArrow: '&laquo;',
	        rightArrow: '&raquo;'
	    }, //다음달 이전달로 넘어가는 화살표 모양 커스텀 마이징 
	    showWeekDays : true ,// 위에 요일 보여주는 옵션 기본값 : true
	    title: "테스트",	//캘린더 상단에 보여주는 타이틀
	    todayHighlight : true ,	//오늘 날짜에 하이라이팅 기능 기본값 :false 
	    toggleActive : true,	//이미 선택된 날짜 선택하면 기본값 : false인경우 그대로 유지 true인 경우 날짜 삭제
	    weekStart : 0 ,//달력 시작 요일 선택하는 것 기본값은 0인 일요일 
	    language : "ko"	//달력의 언어 선택, 그에 맞는 js로 교체해줘야한다.
		});

		$('#inputEndDate').datepicker({
			format: "yyyy-mm-dd",	//데이터 포맷 형식(yyyy : 년 mm : 월 dd : 일 )
		    startDate: '-10d',	//달력에서 선택 할 수 있는 가장 빠른 날짜. 이전으로는 선택 불가능 ( d : 일 m : 달 y : 년 w : 주)
		    endDate: '+10d',	//달력에서 선택 할 수 있는 가장 느린 날짜. 이후로 선택 불가 ( d : 일 m : 달 y : 년 w : 주)
		    autoclose : true,	//사용자가 날짜를 클릭하면 자동 캘린더가 닫히는 옵션
		    calendarWeeks : false, //캘린더 옆에 몇 주차인지 보여주는 옵션 기본값 false 보여주려면 true
		    clearBtn : false, //날짜 선택한 값 초기화 해주는 버튼 보여주는 옵션 기본값 false 보여주려면 true
		    datesDisabled : ['2019-06-24','2019-06-26'],//선택 불가능한 일 설정 하는 배열 위에 있는 format 과 형식이 같아야함.
		    daysOfWeekDisabled : [0,6],	//선택 불가능한 요일 설정 0 : 일요일 ~ 6 : 토요일
		    daysOfWeekHighlighted : [3], //강조 되어야 하는 요일 설정
		    disableTouchKeyboard : false,	//모바일에서 플러그인 작동 여부 기본값 false 가 작동 true가 작동 안함.
		    immediateUpdates: false,	//사용자가 보는 화면으로 바로바로 날짜를 변경할지 여부 기본값 :false 
		    multidate : false, //여러 날짜 선택할 수 있게 하는 옵션 기본값 :false 
		    multidateSeparator :",", //여러 날짜를 선택했을 때 사이에 나타나는 글짜 2019-05-01,2019-06-01
		    templates : {
		        leftArrow: '&laquo;',
		        rightArrow: '&raquo;'
		    }, //다음달 이전달로 넘어가는 화살표 모양 커스텀 마이징 
		    showWeekDays : true ,// 위에 요일 보여주는 옵션 기본값 : true
		    title: "테스트",	//캘린더 상단에 보여주는 타이틀
		    todayHighlight : true ,	//오늘 날짜에 하이라이팅 기능 기본값 :false 
		    toggleActive : true,	//이미 선택된 날짜 선택하면 기본값 : false인경우 그대로 유지 true인 경우 날짜 삭제
		    weekStart : 0 ,//달력 시작 요일 선택하는 것 기본값은 0인 일요일 
		    language : "ko"	//달력의 언어 선택, 그에 맞는 js로 교체해줘야한다.
		});
	*/
});
</script>