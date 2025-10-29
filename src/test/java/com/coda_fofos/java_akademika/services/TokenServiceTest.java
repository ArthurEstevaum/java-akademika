package com.coda_fofos.java_akademika.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import enities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private TokenService tokenService;

    @Mock
    private User mockUser;

    private final String testSecret = "uma-chave-secreta-de-teste-bem-segura-para-hmac256";
    private static final String ISSUER = "akademika-api";
    private final String userEmail = "teste@akademika.com";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", testSecret);
        when(mockUser.getEmail()).thenReturn(userEmail);
    }

    @Test
    void testGenerateTokenSuccess() {
        String token = tokenService.generateToken(mockUser);

        assertNotNull(token);

        DecodedJWT decodedJWT = JWT.decode(token);

        assertEquals(ISSUER, decodedJWT.getIssuer());
        assertEquals(userEmail, decodedJWT.getSubject());
        assertTrue(decodedJWT.getExpiresAtAsInstant().isAfter(Instant.now()));
    }

    @Test
    void testValidateTokenSuccess() {
        String token = tokenService.generateToken(mockUser);

        String subject = tokenService.validateToken(token);

        assertEquals(userEmail, subject);
    }

    @Test
    void testValidateTokenExpired() {
        Algorithm algorithm = Algorithm.HMAC256(testSecret);
        String expiredToken = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userEmail)
                .withExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS))
                .sign(algorithm);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.validateToken(expiredToken);
        });

        assertEquals("Token JWT inválido ou expirado", exception.getMessage());
    }

    @Test
    void testValidateTokenInvalidSignature() {
        String anotherSecret = "outra-chave-secreta-diferente";
        Algorithm algorithm = Algorithm.HMAC256(anotherSecret);
        String tokenWithWrongSignature = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userEmail)
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign(algorithm);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.validateToken(tokenWithWrongSignature);
        });

        assertEquals("Token JWT inválido ou expirado", exception.getMessage());
    }

    @Test
    void testValidateTokenWrongIssuer() {
        Algorithm algorithm = Algorithm.HMAC256(testSecret);
        String tokenWithWrongIssuer = JWT.create()
                .withIssuer("outro-issuer")
                .withSubject(userEmail)
                .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign(algorithm);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.validateToken(tokenWithWrongIssuer);
        });

        assertEquals("Token JWT inválido ou expirado", exception.getMessage());
    }

    @Test
    void testValidateTokenMalformed() {
        String malformedToken = "isto.nao.e.um.token";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.validateToken(malformedToken);
        });

        assertEquals("Token JWT inválido ou expirado", exception.getMessage());
    }

    @Test
    void testGenerateTokenWithNullSecretThrowsException() {
        TokenService serviceWithNullSecret = new TokenService();

        assertThrows(IllegalArgumentException.class, () -> {
            serviceWithNullSecret.generateToken(mockUser);
        });
    }

    @Test
    void testValidateTokenWithNullSecretThrowsException() {
        TokenService serviceWithNullSecret = new TokenService();
        String token = tokenService.generateToken(mockUser);

        assertThrows(IllegalArgumentException.class, () -> {
            serviceWithNullSecret.validateToken(token);
        });
    }
}