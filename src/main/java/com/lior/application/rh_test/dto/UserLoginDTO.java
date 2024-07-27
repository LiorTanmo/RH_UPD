package com.lior.application.rh_test.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class UserLoginDTO {

    @NotEmpty
    String username;

    @NotEmpty
    String password;
}
