package com.lior.application.rh_test.dto;

import com.lior.application.rh_test.util.Roles;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class UserDTO {

    int id;
    @NotEmpty
    private String username;

    private String name;

    private String surname;

    private String parentName;

    private Roles role;

    private Date creation_date;

    private Date last_edit_date;


}
