package com.lior.application.rh_test.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class CommentDTO {

    int id;

    @Size(max = 300, message = "Up to 300 characters")
    @NotNull
    private String text;

    private UserDTO inserted_by;

    private Date creation_date;

}
