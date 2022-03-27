package com.kangong.common.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.beans.factory.annotation.Autowired;

import com.kangong.common.util.BeanUtils;
import com.kangong.user.model.UserVO;
import com.kangong.user.service.UserService;
 
public class UserInfoTag extends SimpleTagSupport{
    
    private String id;
    private String paramType;
    
    @Autowired
    UserService userService;
    //doTag()를 오버라이딩 하여 커스텀 태그 처리 내용을 개발한다.
    @Override
    public void doTag() throws JspException, IOException {
        //JspContext는 이 태그를 사용한 JSP 페이지에 대한 정보를  담고 있다.  
        PageContext context = (PageContext)this.getJspContext();
        
        //태그가 호출된 JSP에 대해 요청 정보를 가져온다.
        HttpServletRequest request =  (HttpServletRequest)context.getRequest();        
        
        //Jsp 페이지의 결과를 브라우저로 출력할 출력스트림를 얻는다.
        //JSP의 out 객체와 같음.
        JspWriter out = context.getOut();
       
        
        UserVO paramVO = new UserVO();
        paramVO.setId(id);
        UserVO userVO = null;
        try {    
        	userService = (UserService)BeanUtils.getBean("userService");
        	userVO = userService.getUser(paramVO);
        }catch(Exception e) {}
        
        if("username".equals(paramType)) {
        	out.print(userVO.getUserName());
        }
        System.out.println("===============================>"+userVO.getUserName());
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
    
}

