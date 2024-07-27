package com.lior.application.rh_test.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

//TODO probably needs adjustments to user

@Getter@Setter
public class NewsDTO {

    private int id;
    @NotNull
    @Size(min = 2, max = 150, message = "Up to 150 characters, at least 2 characters")
    private String title;
    @NotNull
    @Size(min = 2, max = 2000, message = "Up to 2000 characters, at least 2 characters")
    private String text;

    private UserDTO inserted_by;

    private UserDTO updated_by;

    private Date creation_date;

    private Date last_edit_date;

    private Page<CommentDTO> comments;
}
