package com.dsa360.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeTrackerAdvice {
	Logger logger = LoggerFactory.getLogger(ExecutionTimeTrackerAdvice.class);

	//pointcut
	@Around("@annotation(com.dsa360.api.aspect.TrackExecutionTime)")
	public Object trackTime(ProceedingJoinPoint pjp) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object obj = pjp.proceed();
		long endTime = System.currentTimeMillis();
        logger.info("Method Name = {}, time taken to execute {} ms", pjp.getSignature(), (endTime - startTime));
		return obj;

	}

}
