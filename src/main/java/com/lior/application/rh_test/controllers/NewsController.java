package com.lior.application.rh_test.controllers;

import com.lior.application.rh_test.dto.NewsDTO;
import com.lior.application.rh_test.services.NewsService;
import com.lior.application.rh_test.util.ErrorPrinter;
import com.lior.application.rh_test.util.ErrorResponse;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import com.lior.application.rh_test.util.exceptions.NotAuthorizedException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



//TODO
@RestController
@RequestMapping("/news")
@AllArgsConstructor
@Slf4j
public class NewsController {

    private final NewsService newsService;
    private final ErrorPrinter errorPrinter;

    /**
     * Method for posting news
     *
     * @param newsDTO       News body
     * @param bindingResult Error holder
     * @return Response status Created or Exception
     */
    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid NewsDTO newsDTO,
                                             BindingResult bindingResult) {
        errorPrinter.printFieldErrors(bindingResult);
        newsService.save(newsDTO);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    /**
     * Method for editing news
     *
     * @param newsDTO       New news body
     * @param bindingResult Error holder
     * @param id            Id of news to be patched
     * @return Status OK or Exception status
     */
    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> edit(@RequestBody @Valid NewsDTO newsDTO,
                                           BindingResult bindingResult,
                                           @PathVariable(name = "id") int id) {
        errorPrinter.printFieldErrors(bindingResult);
        newsService.update(id, newsDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * Get all news by pages
     *
     * @param page        Page number
     * @param newsPerPage News per page (required)
     * @return Page of news with null comments (required)
     */
    @GetMapping()
    public Page<NewsDTO> getNews(@RequestParam Integer page,
                                 @RequestParam Integer newsPerPage) {
        if (page < 1 || newsPerPage < 1){
            throw new IllegalArgumentException("Page number and size can't be less than 1");
        }
        return newsService.findAll(page, newsPerPage);
    }

    /**
     * Get single news post with paginated comments
     *
     * @param id              News Id
     * @param page            Comment page number (required), starting with 1
     * @param commentsPerPage How many comments per page (required)
     * @return News with comments
     */
    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable("id") int id,
                                               @RequestParam Integer page,
                                               @RequestParam Integer commentsPerPage) {
        if (page < 1 || commentsPerPage < 1){
            throw new IllegalArgumentException("Page number and size can't be less than 1");
        }
        return ResponseEntity.ok(newsService.findOne(id, page, commentsPerPage));
    }

    /**
     * News search. Looks for searched string in title and text. Case-sensitive.
     *
     * @param query Search string
     * @param page Page number of results (starting with 1)
     * @param newsPerPage How many 'news' are displayed per page
     * @return List of news corresponding to search request.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<NewsDTO>> newsSearch(@RequestParam String query,
                                             @RequestParam int page,
                                             @RequestParam int newsPerPage) {
        if (query.isBlank()){
            throw new IllegalArgumentException( "Search query can't be blank.");
        }
        if (page < 1 || newsPerPage < 1){
            throw new IllegalArgumentException("Page number and size can't be less than 1");
        }
        return ResponseEntity.ok(newsService.search(query, page, newsPerPage));
    }

    /**
     * Method for deleting news. Corresponding comments are cascade-deleted.
     *
     * @param id News id
     * @return HttpStatus NO_CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable(name = "id") int id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(HttpMessageNotReadableException e) {
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
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
