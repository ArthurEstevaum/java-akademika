package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.dtos.LoginRequestDTO;
import com.coda_fofos.java_akademika.dtos.UserRegistrationDTO;
import com.coda_fofos.java_akademika.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.coda_fofos.java_akademika.entities.User;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        var userDTO = new UserRegistrationDTO("Test User", "test@example.com", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotRegisterUserWithSameEmail() throws Exception {
        var userDTO1 = new UserRegistrationDTO("Test User 1", "test@example.com", "password123");
        var userDTO2 = new UserRegistrationDTO("Test User 2", "test@example.com", "password456");

        // Register the first user
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO1)))
                .andExpect(status().isOk());

        // Try to register the second user with the same email
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfullyWithRegisteredUser() throws Exception {
        // First, register a user
        String rawPassword = "password123";
        User user = new User("Test User", bCryptPasswordEncoder.encode(rawPassword), "test@example.com");
        userRepository.save(user);

        var loginDTO = new LoginRequestDTO("test@example.com", rawPassword);

        // Then, attempt to login
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void shouldNotLoginWithWrongPassword() throws Exception {
        // First, register a user
        String rawPassword = "password123";
        User user = new User("Test User", bCryptPasswordEncoder.encode(rawPassword), "test@example.com");
        userRepository.save(user);

        var loginDTO = new LoginRequestDTO("test@example.com", "wrongpassword");

        // Then, attempt to login with wrong password
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotLoginWithNonExistentUser() throws Exception {
        var loginDTO = new LoginRequestDTO("nonexistent@example.com", "password123");

        // Attempt to login with a user that is not registered
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }
}

