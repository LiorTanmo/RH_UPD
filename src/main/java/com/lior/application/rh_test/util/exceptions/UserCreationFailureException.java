package com.lior.application.rh_test.util.exceptions;

public class UserCreationFailureException extends RuntimeException{
    public UserCreationFailureException(String msg){
        super(msg);
    }
}
