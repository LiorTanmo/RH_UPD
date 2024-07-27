package com.lior.application.rh_test.services;

import com.lior.application.rh_test.model.User;
import com.lior.application.rh_test.repos.UsersRepository;
import com.lior.application.rh_test.security.UserAccountDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserInfoService implements UserDetailsService {


    private final UsersRepository usersRepository;
    //spring security user details service realisation
    @Cacheable(value = "users", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = usersRepository.findByUsername(username);
        if (user.isEmpty())
            throw new UsernameNotFoundException("User " + username +" not found");
        return new UserAccountDetails(user.get());
    }
}
