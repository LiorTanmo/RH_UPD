package com.lior.application.rh_test.controllers;

import com.lior.application.rh_test.dto.UserCRUDDTO;
import com.lior.application.rh_test.dto.UserDTO;
import com.lior.application.rh_test.services.UsersService;
import com.lior.application.rh_test.util.*;
import com.lior.application.rh_test.util.exceptions.NotAuthorizedException;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import com.lior.application.rh_test.util.exceptions.UserNotFoundException;
import com.lior.application.rh_test.util.exceptions.ValidationFailureException;
import jakarta.validation.Valid;
import jdk.jfr.ContentType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


//TODO
@Slf4j
@RestController
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@AllArgsConstructor
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final UserValidator userValidator;
    private final ErrorPrinter errorPrinter;

    /**
     * User info by username
     * @param username Username of the User, or current user if username is missing
     * @return UserDTO
     */
    @GetMapping("/info/{username}")
    public ResponseEntity<UserDTO> userPage(@PathVariable(name = "username", required = false) String username) {
        return ResponseEntity.ok(usersService.findUserByUsername(username));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDTO> userPage(@RequestParam int id) {
        return ResponseEntity.ok(usersService.findOne(id));
    }

    /**
     * Loads all users
     *
     * @return List with all Users information
     */

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(@RequestParam int page, int usersPerPage) {
        return ResponseEntity.ok(usersService.getAllUsers(page, usersPerPage));
    }

    /**
     * Method for creating new users. Admin Only
     * Will automatically generate random password if no password provided
     * @param user User information
     * @param bindingResult Field Error holder
     * @return HttpStatus created or error response
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registration")
    public ResponseEntity<Object> registration(@RequestBody @Valid UserCRUDDTO user,
                                                    BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        errorPrinter.printFieldErrors(bindingResult);
        if (user.getRole() == null) {
            user.setRole(Roles.ROLE_SUBSCRIBER);
        }
        String password = usersService.save(user);
        if (password.isEmpty()){
            return ResponseEntity.ok(HttpStatus.CREATED);
        }
        return ResponseEntity.ok("Your generated password is: " + password);
    }

    /**
     * Edit user info. Changing roles is available only for admins
     *
     * @param user          New user info
     * @param bindingResult Field error holder
     * @param id      id of user to be changed
     * @return HttpStatus OK or error response
     */
    @PatchMapping("/info/id/{id}")
    public ResponseEntity<HttpStatus> updateUserById(@RequestBody @Valid UserCRUDDTO user,
                                           BindingResult bindingResult,
                                           @PathVariable int id) {
        userValidator.validate(user, bindingResult);
        errorPrinter.printFieldErrors(bindingResult);
        usersService.updateById(id, user);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    /**
     * Edit user info. Changing roles is available only for admins
     *
     * @param user          New user info
     * @param bindingResult Field error holder
     * @param username      username of user to be changed
     * @return HttpStatus OK or error response
     */
    @PatchMapping("/info/{username}")
    public ResponseEntity<HttpStatus> updateUserByUsername(@RequestBody @Valid UserCRUDDTO user,
                                                     BindingResult bindingResult,
                                                     @PathVariable String username) {
        userValidator.validate(user, bindingResult);
        errorPrinter.printFieldErrors(bindingResult);
        usersService.updateByUsername(user, username);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * Deleting user by Username
     *
     * @param username Target User's username
     * @return HttpStatus No_Content or Error response
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<HttpStatus> deleteUserByUsername(@PathVariable(name = "username") String username){
        usersService.delete(username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deleting user by ID
     *
     * @param id Target User's id
     * @return HttpStatus No_Content or Error response
     */
    @DeleteMapping()
    public ResponseEntity<HttpStatus> deleteUser(@RequestParam(name = "id") int id ) {
        usersService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //region Util
    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(HttpMessageNotReadableException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(ValidationFailureException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(NotAuthorizedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(NotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


//endregion

}
