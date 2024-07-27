package com.lior.application.rh_test.util.exceptions;

public class NotAuthorizedException extends IllegalAccessException{

    public NotAuthorizedException (String msg){
        super(msg);
    }
}
