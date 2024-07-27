package com.lior.application.rh_test.aspect;

import com.lior.application.rh_test.aspect.util.OwnershipFilter;
import com.lior.application.rh_test.repos.CommentsRepository;
import com.lior.application.rh_test.repos.NewsRepository;
import com.lior.application.rh_test.repos.UsersRepository;
import com.lior.application.rh_test.util.exceptions.NotAuthorizedException;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;


@Aspect
@Component
@AllArgsConstructor
public class MethodSecurity {

    private final NewsRepository newsRepository;
    private final UsersRepository usersRepository;
    private final CommentsRepository commentsRepository;

    @Before(value = "@annotation(ownershipFilter)")
    public void logExecutionTime(JoinPoint joinPoint, OwnershipFilter ownershipFilter) throws Throwable {

        String ownerUsername= "";
       switch (ownershipFilter.datatype()){
           case News -> ownerUsername = newsRepository.findById((Integer) joinPoint.getArgs()[0])
                   .orElseThrow(NotFoundException::new).getInserted_by().getUsername();
           case Comment -> ownerUsername = commentsRepository.findById((Integer) joinPoint.getArgs()[0])
                   .orElseThrow(NotFoundException::new).getInserted_by().getUsername();
           case User -> ownerUsername = usersRepository.findById((Integer) joinPoint.getArgs()[0])
                   .orElseThrow(NotFoundException::new).getUsername();
       }

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SimpleGrantedAuthority> roles = (List<SimpleGrantedAuthority>) SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();
        //checks if active user is Admin or Owner
        if (roles.stream().noneMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"))
                || !name.equals(ownerUsername)){
            String msg = "You can delete or alter ";
            switch (ownershipFilter.datatype()){
                case User -> msg += "only your own user profile.";
                case News -> msg += "only the news you posted.";
                case Comment -> msg += "only your own comments.";
            }
            throw new NotAuthorizedException(msg);
        }
    }



}
