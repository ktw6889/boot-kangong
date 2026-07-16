package com.kangong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
@EnableJpaAuditing
public class KangongApplication {

	public static void main(String[] args) {

		 try {
			 	SpringApplication.run(KangongApplication.class, args);
		    } catch (Throwable e) {
		        if(e.getClass().getName().contains("SilentExitException")) {
		            log.info("Spring is restarting the main thread - See spring-boot-devtools");
		        } else {
		        	log.error("Application crashed!", e);
		        }
		    }
	}
}
