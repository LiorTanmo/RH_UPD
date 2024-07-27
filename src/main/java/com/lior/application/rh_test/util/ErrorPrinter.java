package com.lior.application.rh_test.util;

import com.lior.application.rh_test.util.exceptions.ValidationFailureException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Component
public class ErrorPrinter {
    public void printFieldErrors(BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors){
                errMsg.append(error.getField())
                        .append(" – ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new ValidationFailureException(errMsg.toString());
        }
    }
}
