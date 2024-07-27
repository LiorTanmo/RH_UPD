package com.lior.application.rh_test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lior.application.rh_test.config.JWTFilter;
import com.lior.application.rh_test.controllers.UsersController;
import com.lior.application.rh_test.dto.UserCRUDDTO;
import com.lior.application.rh_test.dto.UserDTO;
import com.lior.application.rh_test.model.User;
import com.lior.application.rh_test.security.JWTUtil;
import com.lior.application.rh_test.services.UsersService;
import com.lior.application.rh_test.util.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Profile("testing")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = UsersController.class)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@TestPropertySource(locations = "classpath:application.yml")
public class UsersControllerTest {
    @Autowired
    private MockMvc mockmvc;
    @MockBean
    private JWTFilter jwtFilter;
    @MockBean
    private UsersService usersService;
    @MockBean
    private UserValidator userValidator;
    @MockBean
    private ErrorPrinter errorPrinter;
    @MockBean
    JWTUtil jwtUtil;

    UserCRUDDTO request;
    UserDTO userDTO;


    @BeforeEach
    public void setup() {
        request = UserCRUDDTO.builder()
                .username("TestUsername")
                .name("name")
                .surname("surname")
                .parentName("parentName")
                .build();

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("Username");
        userDTO.setId(1);

        when(usersService.findUserByUsername(userDTO.getUsername())).thenReturn(userDTO);
        when(usersService.findOne(userDTO.getId())).thenReturn(userDTO);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void registrationTestStatus200() throws Exception {
        when(usersService.save(any())).thenReturn("password");

        mockmvc.perform(post("/users/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetUserInfoByUsernameThenStatus200() throws Exception {
        mockmvc.perform(get("/users/info/{username}", userDTO.getUsername()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(status().isOk());
    }

    @Test
    public void gettingUserByIdTest() throws Exception {
        mockmvc.perform(get("/users/info").param("id", String.valueOf((userDTO.getId()))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(status().isOk());
    }

    @Test
    public void patchTestStatus200() throws Exception {
        try (MockedStatic<CurrentUser> util = mockStatic(CurrentUser.class)) {
            util.when(CurrentUser::get).thenReturn(User.
                    builder().username("Admin").role(Roles.ROLE_ADMIN).build());
            mockmvc.perform(patch("/users/info/{username}", "TestUsername")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void userDeletingTest() throws Exception {
        mockmvc.perform(delete("/users/{username}", "Username")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


}
