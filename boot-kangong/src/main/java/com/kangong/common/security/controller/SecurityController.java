package com.kangong.common.security.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RequestMapping("/security/*") 
@Controller
public class SecurityController {

	@GetMapping("/all")
	  public void doAll() {
	    
	    log.info("do all can access everybody");
	  }
	  
	  @GetMapping("/member")
	  public void doMember() {
	    
	    log.info("logined member");
	  }
	  
	  @GetMapping("/admin")
	  public void doAdmin() {
	    
	    log.info("admin only");
	  }  
	  
	  
	  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MEMBER')")
	  @GetMapping("/annoMember")
	  public void doMember2() {
	    
	    log.info("logined annotation member");
	  }
	  
	  
	  @Secured({"ROLE_ADMIN"})
	  @GetMapping("/annoAdmin")
	  public void doAdmin2() {

	    log.info("admin annotaion only");
	  }
	  
	  
	  @GetMapping("/accessError")
		public void accessDenied(Authentication auth, Model model) {

			log.info("access Denied : " + auth);

			model.addAttribute("msg", "Access Denied");
		}

		@GetMapping("/customLogin")
		public String loginInput(String error, String logout, Model model) {

			log.info("error: " + error);
			log.info("logout: " + logout);

			if (error != null) {
				model.addAttribute("error", "Login Error Check Your Account");
			}

			if (logout != null) {
				model.addAttribute("logout", "Logout!!");
			}
			
			return "kims:/security/customLogin";
		}

		@GetMapping("/customLogout")
		public void logoutGET() {

			log.info("custom logout");
		}

		@PostMapping("/customLogout")
		public void logoutPost() {

			log.info("post custom logout");
		}

}
