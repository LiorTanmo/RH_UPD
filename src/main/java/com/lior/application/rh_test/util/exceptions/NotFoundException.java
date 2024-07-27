package com.lior.application.rh_test.util.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private final String msg;

    public NotFoundException(){
        this.msg = "Couldn't find data corresponding to your request";
    }

    public NotFoundException(String msg){
        this.msg= msg;
    }
}
