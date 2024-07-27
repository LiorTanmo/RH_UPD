package com.lior.application.rh_test.aspect;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lior.application.rh_test.util.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(value = {JWTVerificationException.class})
//    public ResponseEntity<Object> handleJWT(JWTVerificationException e, WebRequest webRequest) {
//        log.error("Handling: ", e);
//        HttpStatus errorCode = x;
//        return this.handleExceptionInternal(exception, new ErrorResponse("Unexpected Internal Server Error occurred"),
//                new HttpHeaders(), HttpStatus.UNAUTHORIZED, webRequest);
//    }
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleGenericExceptions(Exception exception, WebRequest webRequest) {
        log.error("Handling: ", exception);
        HttpStatus errorCode = HttpStatus.INTERNAL_SERVER_ERROR;
        return this.handleExceptionInternal(exception, new ErrorResponse("Unexpected Internal Server Error occurred"),
                new HttpHeaders(), errorCode, webRequest);
    }
}