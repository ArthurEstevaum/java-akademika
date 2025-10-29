package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.dtos.LoginRequestDTO;
import com.coda_fofos.java_akademika.dtos.LoginResponseDTO;
import com.coda_fofos.java_akademika.dtos.RegisterUserRequestDTO;
import com.coda_fofos.java_akademika.repositories.UserRepository;
import enities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private final String userEmail = "test@example.com";
    private final String userPassword = "password123";
    private final String userEncodedPassword = "encodedPasswordABC";

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", userEncodedPassword, userEmail);
    }

    // --- Testes para authenticateUser ---

    @Test
    void testAuthenticateUser_Success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO(userEmail, userPassword);
        String expectedToken = "mock.jwt.token";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(userPassword, userEncodedPassword)).thenReturn(true);
        when(tokenService.generateToken(testUser)).thenReturn(expectedToken);

        LoginResponseDTO response = authService.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals(userEmail, response.email());
        assertEquals(expectedToken, response.token());

        verify(userRepository).findByEmail(userEmail);
        verify(passwordEncoder).matches(userPassword, userEncodedPassword);
        verify(tokenService).generateToken(testUser);
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("wrong@example.com", userPassword);

        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("Email não encontrado ou senha inválida.", exception.getMessage());

        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void testAuthenticateUser_WrongPassword() {
        String wrongPassword = "wrongPassword123";
        LoginRequestDTO loginRequest = new LoginRequestDTO(userEmail, wrongPassword);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(wrongPassword, userEncodedPassword)).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticateUser(loginRequest);
        });

        assertEquals("Email não encontrado ou senha inválida.", exception.getMessage());

        verify(tokenService, never()).generateToken(any());
    }


    // --- Testes para registerNewUser ---

    @Test
    void testRegisterNewUser_Success() {
        String newEmail = "new@example.com";
        String newPassword = "newPassword456";
        String newUsername = "newUser";
        String newEncodedPassword = "encodedNewPasswordXYZ";
        RegisterUserRequestDTO registerRequest = new RegisterUserRequestDTO(newUsername, newPassword, newEmail);

        when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        authService.registerNewUser(registerRequest);

        verify(userRepository).findByEmail(newEmail);
        verify(passwordEncoder).encode(newPassword);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals(newUsername, savedUser.getUsername());
        assertEquals(newEmail, savedUser.getEmail());
        assertEquals(newEncodedPassword, savedUser.getPassword());
    }

    @Test
    void testRegisterNewUser_EmailAlreadyInUse() {
        RegisterUserRequestDTO registerRequest = new RegisterUserRequestDTO("anotherUser", userPassword, userEmail);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerNewUser(registerRequest);
        });

        assertEquals("Erro: Email já está em uso!", exception.getMessage());

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}
