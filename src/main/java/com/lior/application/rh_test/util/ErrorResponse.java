package com.lior.application.rh_test.util;

import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter@Setter
public class ErrorResponse {
    private String message;
    private String timestamp;

    //response form
    public ErrorResponse(String message) {
        this.message = message;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.timestamp = dtf.format(LocalDateTime.now());
    }
}
