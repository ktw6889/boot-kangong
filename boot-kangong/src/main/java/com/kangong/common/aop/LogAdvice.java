package com.kangong.common.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.log4j.Log4j2;

@Aspect
@Log4j2
@Component
public class LogAdvice {

	@Before("execution(* com.kangong..*.*(..))")
	public void logBefore() {
		// log.info("logBefore=========================================");
	}

	@AfterThrowing(pointcut = "execution(* com.kangong..*.*(..))", throwing = "exception")
	public void logException(Exception exception) {

		log.info("Exception....!!!!");
		log.info("exception: " + exception);

		exception.printStackTrace();
	}

	@Around("execution(* com.kangong.sample.controller.SampleController*.*(..))")
	public Object logTime(ProceedingJoinPoint pjp) {

		long start = System.currentTimeMillis();
		log.info("Target: " + pjp.getTarget());
		log.info("Param: " + Arrays.toString(pjp.getArgs()));

		// invoke method
		Object result = null;

		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();

		log.info("TIME: " + (end - start));

		return result;
	}

	@Around("@annotation(com.kangong.common.annotation.LogExecutionTime)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.debug("시작================>");
		Object proceed = joinPoint.proceed();
		stopWatch.stop();
		log.debug("끝================>" + stopWatch.prettyPrint());
		System.out.println("annotation======>");

		return proceed;
	}

}
