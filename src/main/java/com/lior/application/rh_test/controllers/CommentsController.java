package com.lior.application.rh_test.controllers;

//TODO Testing and access

import com.lior.application.rh_test.dto.CommentDTO;
import com.lior.application.rh_test.dto.UserDTO;
import com.lior.application.rh_test.model.Comment;
import com.lior.application.rh_test.services.CommentsService;
import com.lior.application.rh_test.util.*;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import com.lior.application.rh_test.util.exceptions.NotAuthorizedException;
import com.lior.application.rh_test.util.exceptions.ValidationFailureException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;
    private final ErrorPrinter errorPrinter;
    private final ModelMapper modelMapper;

    /**
     * Method for commenting news
     *
     * @param commentDTO    Comment information
     * @param bindingResult Error holder
     * @param id            Id of the news that are being commented
     * @return HttpStatusResponse
     */
    @PostMapping("/news/{news_id}")
    public ResponseEntity<HttpStatus> addComment(@RequestBody @Valid CommentDTO commentDTO,
                                                 BindingResult bindingResult,
                                                 @PathVariable(name = "news_id") int id) {
        if (bindingResult.hasErrors()) errorPrinter.printFieldErrors(bindingResult);
        commentsService.addComment(commentDTO, id);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    /**
     * Gets all comments attached to News with given id
     *
     * @param news_id         News id
     * @param page            Page (starting from 1)
     * @param commentsPerPage Comments per page
     * @return Page of comments
     */
    @GetMapping("/news/{news_id}")
    public Page<CommentDTO> getComments(@PathVariable(name = "news_id") int news_id,
                                        @RequestParam Integer page,
                                        @RequestParam Integer commentsPerPage) {
        if (page < 1 || commentsPerPage < 1){
            throw new IllegalArgumentException("Page number and size can't be less than 1");
        }
        return commentsService.findAll(news_id, page, commentsPerPage);
    }

    /**
     * Comment altering method
     *
     * @param commentDTO    Comment body
     * @param bindingResult Field Error holder
     * @param id            Comment id
     * @return OK or Exception response
     */
    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> editComment(@RequestBody @Valid CommentDTO commentDTO,
                                                  BindingResult bindingResult,
                                                  @PathVariable int id) {
        if (bindingResult.hasErrors()) errorPrinter.printFieldErrors(bindingResult);
        commentsService.editComment(id, commentDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * Deleting comment on news by comment number
     *
     * @param id Comment id
     * @return No Content or Exception response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> removeComment(@PathVariable(name = "id") int id) {
        commentsService.removeComment(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

//    /**
//     * Deletes all comments on news with given id. Only accessible by admins.
//     * @param news_id News id
//     * @return No Content or Exception response
//     */
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @DeleteMapping("news/{news_id}")
//    public ResponseEntity<HttpStatus> clearComments(@PathVariable(name = "news_id") int news_id){
//        commentsService.clearCommentsByNewsId(news_id);
//        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
//    }

//region Util
    //Exception Handlers

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(ValidationFailureException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(NotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getMsg());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(NotAuthorizedException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(IllegalArgumentException e){
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(HttpMessageNotReadableException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //DTO Converters


    //endregion
}
