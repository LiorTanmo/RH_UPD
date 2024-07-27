package com.lior.application.rh_test.dto;

import com.lior.application.rh_test.util.Roles;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCRUDDTO {

    @NotEmpty(message = "username can't be empty")
    private String username;

    //@NotEmpty(message = "password can't be empty")
    private String password;

    @NotEmpty(message = "name can't be empty")
    private String name;

    @NotEmpty(message = "surname can't be empty")
    private String surname;

    @NotEmpty(message = "parentName can't be empty")
    private String parentName;

    //@NotEmpty(message = "role can't be empty")
    private Roles role;
}
