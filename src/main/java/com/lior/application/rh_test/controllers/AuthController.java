package com.lior.application.rh_test.controllers;


import com.lior.application.rh_test.dto.UserLoginDTO;
import com.lior.application.rh_test.security.JWTUtil;
import com.lior.application.rh_test.util.ErrorPrinter;
import com.lior.application.rh_test.util.ErrorResponse;
import com.lior.application.rh_test.util.exceptions.ValidationFailureException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final ErrorPrinter errorPrinter;
    private final JWTUtil jwtUtil;

    /**
     * Method for user authentication.
     *
     * @param loginDTO      Credentials
     * @param bindingResult Field error holder
     * @return OK status with Authorisation header, containing JWT or Exception Handler response
     */
    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody @Valid UserLoginDTO loginDTO,
                                            BindingResult bindingResult) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                        loginDTO.getPassword());

        errorPrinter.printFieldErrors(bindingResult);

        authManager.authenticate(authInputToken);
        String jwt = jwtUtil.generateToken(loginDTO.getUsername());
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + jwt);
        return ResponseEntity.ok().headers(headers).body(HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(BadCredentialsException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
