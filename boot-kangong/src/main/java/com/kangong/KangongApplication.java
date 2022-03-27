package com.kangong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KangongApplication {
	
	public static void main(String[] args) {
		
		 try {
			 	SpringApplication.run(KangongApplication.class, args);
		    } catch (Throwable e) {
		        if(e.getClass().getName().contains("SilentExitException")) {
		            System.out.println("Spring is restarting the main thread - See spring-boot-devtools");
		        } else {
		        	System.out.println("Application crashed!");
		        }
		    }
	}
}
