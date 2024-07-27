package com.lior.application.rh_test.util.exceptions;

public class ValidationFailureException extends RuntimeException{
    public ValidationFailureException(String msg){
        super(msg);
    }
}
