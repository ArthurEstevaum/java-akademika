package com.coda_fofos.java_akademika.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import enities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Serviço responsável por gerar e validar tokens JWT.
 */
@Service
public class TokenService {

    // Lê a chave secreta do application.properties (ou application.yml)
    // Crie esta propriedade no seu application.properties! Ex: jwt.secret=minha-chave-secreta-muito-longa
    @Value("${api.security.token.secret}")
    private String secret;

    // Define o emissor do token (identifica sua aplicação)
    private static final String ISSUER = "akademika-api";

    /**
     * Gera um token JWT para um usuário autenticado.
     *
     * @param user O usuário para o qual o token será gerado.
     * @return O token JWT como uma String.
     * @throws RuntimeException Se ocorrer um erro durante a criação do token.
     */
    public String generateToken(User user) {
        try {
            // Define o algoritmo de assinatura usando a chave secreta (HMAC256 neste caso)
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Cria o token com as claims (informações)
            String token = JWT.create()
                    .withIssuer(ISSUER) // Emissor do token
                    .withSubject(user.getEmail()) // Assunto do token (identificador do usuário, email é comum)
                    .withExpiresAt(generateExpirationDate()) // Data de expiração do token
                    // Você pode adicionar outras claims customizadas se precisar (ex: roles)
                    // .withClaim("userId", user.getId())
                    .sign(algorithm); // Assina o token com o algoritmo e a chave
            return token;
        } catch (JWTCreationException exception) {
            // Logar o erro seria uma boa prática aqui
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Valida um token JWT e retorna o assunto (subject), geralmente o email do usuário.
     *
     * @param token O token JWT a ser validado.
     * @return O assunto (email) extraído do token se for válido.
     * @throws RuntimeException Se o token for inválido ou expirar.
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // Verifica a assinatura, o emissor e a expiração do token
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token) // Verifica o token
                    .getSubject(); // Retorna o subject (email)
        } catch (JWTVerificationException exception) {
            // Logar o erro seria uma boa prática
            // Retorna vazio ou lança exceção para indicar falha na validação
            // Lançar exceção é geralmente melhor para o Spring Security lidar
             throw new RuntimeException("Token JWT inválido ou expirado", exception);
            // return "";
        }
    }

    /**
     * Gera a data de expiração para o token (ex: 2 horas a partir de agora).
     * @return Um Instant representando a data/hora de expiração.
     */
    private Instant generateExpirationDate() {
        // Define a duração do token (ex: 2 horas)
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")); // Ajuste o fuso horário conforme necessário (Recife GMT-3)
    }
}