package com.lior.application.rh_test.util;

import com.lior.application.rh_test.dto.UserCRUDDTO;
import com.lior.application.rh_test.model.User;
import com.lior.application.rh_test.services.UsersService;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final UsersService usersService;

    public UserValidator(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserCRUDDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        try {
            usersService.findUserByUsername(user.getUsername());
        } catch (NotFoundException ignored){
        }
        errors.rejectValue("username", "400", "Username already taken");
    }
}
