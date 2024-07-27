package com.lior.application.rh_test.util;

import com.lior.application.rh_test.model.User;
import com.lior.application.rh_test.security.UserAccountDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
//gets current user from Spring Session
//
public class CurrentUser {
    public static User get()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Just in case, shouldn't happen
        if (authentication.getPrincipal() == "anonymousUser"){
            return User.builder().name("anon").username("anonymousUser").build();
        }

        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserAccountDetails userAccountDetails = (UserAccountDetails) authentication.getPrincipal();
        return userAccountDetails.getUser();
    }
}
