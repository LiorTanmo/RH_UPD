package com.lior.application.rh_test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lior.application.rh_test.config.JWTFilter;
import com.lior.application.rh_test.controllers.CommentsController;
import com.lior.application.rh_test.dto.CommentDTO;
import com.lior.application.rh_test.security.JWTUtil;
import com.lior.application.rh_test.services.CommentsService;
import com.lior.application.rh_test.util.ErrorPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Profile("testing")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = CommentsController.class)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@TestPropertySource(locations = "classpath:application.yml")
public class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentsService commentsService;
    @MockBean
    private ErrorPrinter errorPrinter;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private JWTFilter jwtFilter;
    @MockBean
    private JWTUtil jwtUtil;
    ObjectMapper mapper = new ObjectMapper();
    private final CommentDTO commentDTO = new CommentDTO();
    private final CommentDTO commentDTO2 = new CommentDTO();

    @BeforeEach
    public void setup(){

        final int news_id = 1;
        final int page = 1;
        final int commentsPerPage = 10;

        commentDTO.setId(1);
        commentDTO.setText("Text");
        commentDTO2.setId(2);
        commentDTO2.setText("Text2");

        when(commentsService.findAll(news_id, page, commentsPerPage))
                .thenReturn(new PageImpl<>(List.of(commentDTO, commentDTO2)));
    }

    @Test
    public void shouldAddCommentAndRespondOk() throws Exception {

        mockMvc.perform(post("/comments/news/{news_id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetCommentsAndRespondOk() throws Exception {
        mockMvc.perform(get("/comments/news/{news_id}", 1)
                        .param("page", "1")
                        .param("commentsPerPage", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].id").value(2));
    }

    @Test
    public void shouldGetCommentsAndRespondBadRequest() throws Exception {
        mockMvc.perform(get("/comments/news/{news_id}", 1)
                        .param("page", "-1")
                        .param("commentsPerPage", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldEditCommentAndRespondOk() throws Exception {
        mockMvc.perform(patch("/comments/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDTO)))
                .andExpect(status().isOk());
    }


}
