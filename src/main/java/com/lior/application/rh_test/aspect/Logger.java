package com.lior.application.rh_test.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class Logger {
    //Pointcuts
    private static final String controllerCut = "@within(org.springframework.web.bind.annotation.RestController)";
    private static final String serviceCut = "@within(org.springframework.stereotype.Service)";

   // private static final String exceptionCut ="within(com.lior.application.rh_test..*)";

    @Before(controllerCut)
    public void logControllerRequest(JoinPoint joinPoint) {
        log.info("Invoked controller method \"" + joinPoint.getSignature().getName() + "\" with arguments" + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = controllerCut, returning = "response")
    public void logControllerResponse(JoinPoint joinPoint, Object response) {
        // for log the controller name
       log.info("Method \"" + joinPoint.getSignature() + "\" responded: " +  response.toString());
    }

//    @AfterThrowing(pointcut = exceptionCut,throwing = "e")
//    public void exceptionLogger(JoinPoint joinPoint, Throwable e){
//        log.error("Method \"" + joinPoint.getSignature() + "\" threw exception " + e, e);
//    }

    @Before(serviceCut)
    public void logServiceRequest(JoinPoint joinPoint) {
        log.info("Invoked service method \"" + joinPoint.getSignature().getName()  + "\" with arguments " + Arrays.toString(joinPoint.getArgs()));
    }

}
