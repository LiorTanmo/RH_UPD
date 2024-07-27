package com.lior.application.rh_test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lior.application.rh_test.config.JWTFilter;
import com.lior.application.rh_test.controllers.NewsController;
import com.lior.application.rh_test.controllers.UsersController;
import com.lior.application.rh_test.dto.NewsDTO;
import com.lior.application.rh_test.dto.UserCRUDDTO;
import com.lior.application.rh_test.model.News;
import com.lior.application.rh_test.model.User;
import com.lior.application.rh_test.security.JWTUtil;
import com.lior.application.rh_test.services.NewsService;
import com.lior.application.rh_test.services.UsersService;
import com.lior.application.rh_test.util.CurrentUser;
import com.lior.application.rh_test.util.ErrorPrinter;
import com.lior.application.rh_test.util.Roles;
import com.lior.application.rh_test.util.UserValidator;
import com.lior.application.rh_test.util.exceptions.NotAuthorizedException;
import com.lior.application.rh_test.util.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Profile("testing")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = NewsController.class)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@TestPropertySource(locations = "classpath:application.yml")
public class NewsControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JWTFilter jwtFilter;
    @MockBean
    private ErrorPrinter errorPrinter;
    @MockBean
    private JWTUtil jwtUtil;
    @MockBean
    private NewsService newsService;

    ObjectMapper objectMapper = new ObjectMapper();
    NewsDTO newsDTO = new NewsDTO();

    @BeforeEach
    public void setup() {
        newsDTO.setId(1);
        newsDTO.setTitle("Title");
        newsDTO.setText("Text");
    }

    @Test
    public void whenSearchNewsThenStatus200() throws Exception {
        when(newsService.search(eq("Text"), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(newsDTO)));
        mockMvc.perform(get("/news/search")
                        .param("query", "Text")
                        .param("page", "1")
                        .param("newsPerPage", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Title"))
                .andExpect(jsonPath("$.content[0].text").value("Text"));
    }

    @Test
    public void whenSearchNewsWithBadArgsThenStatus400() throws Exception {
        mockMvc.perform(get("/news/search")
                        .param("query", "Text")
                        .param("page", "1")
                        .param("newsPerPage", "0"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/news/search")
                        .param("query", "")
                        .param("page", "1")
                        .param("newsPerPage", "10"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/news/search")
                        .param("page", "1")
                        .param("newsPerPage", "10"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/news/search")
                        .param("query", "Text")
                        .param("page", "0")
                        .param("newsPerPage", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenSearchNewsThenStatus404() throws Exception {
        when(newsService.search(anyString(), anyInt(), anyInt())).thenThrow(new NotFoundException());

        mockMvc.perform(get("/news/search")
                        .param("query", "Txt")
                        .param("page", "1")
                        .param("newsPerPage", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetNewsThenStatus200() throws Exception {
        when(newsService.findOne(eq(1), anyInt(), anyInt())).thenReturn(newsDTO);

        mockMvc.perform(get("/news/{id}", 1)
                        .param("page", "1")
                        .param("commentsPerPage", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetNewsWithIllegalArgumentsThenStatus400() throws Exception {

        mockMvc.perform(get("/news/{id}", 1)
                        .param("page", "1")
                        .param("commentsPerPage", "0"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/news/{id}", 1)
                        .param("page", "0")
                        .param("commentsPerPage", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenGetNewsThenStatus404() throws Exception {
        when(newsService.findOne(anyInt(), anyInt(), anyInt())).thenThrow(new NotFoundException());
        mockMvc.perform(get("/news/{id}", 2)
                        .param("page", "1")
                        .param("commentsPerPage", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenPostNewsThenStatus200() throws Exception {
        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsDTO)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenPatchNewsThenStatus200() throws Exception {
        try (MockedStatic<CurrentUser> util = mockStatic(CurrentUser.class)) {
            util.when(CurrentUser::get).thenReturn(User.
                    builder().username("Admin").role(Roles.ROLE_ADMIN).build());
            mockMvc.perform(patch("/news/{id}", 1)
                            .content(objectMapper.writeValueAsString(newsDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void userDeletingTest() throws Exception {
        mockMvc.perform(delete("/news/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void whenBadJSONThenStatus400() throws Exception {
        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(" "))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/news/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(" "))
                .andExpect(status().isBadRequest());
    }

//TODO починить тест, аспектовое исключение не видно в моке
    @Test
    public void whenPatchOrDeleteThenForbidden() throws Exception {
        doThrow(new NotAuthorizedException("")).when(newsService).delete(anyInt());
        doThrow(new NotAuthorizedException("")).when(newsService).update(anyInt(), any(NewsDTO.class));

        mockMvc.perform(delete("news/{id}", 1))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/news/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsDTO)))
                .andExpect(status().isForbidden());
    }

}
