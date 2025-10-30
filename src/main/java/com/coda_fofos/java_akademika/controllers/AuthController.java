package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.dtos.LoginRequestDTO;
import com.coda_fofos.java_akademika.dtos.LoginResponseDTO;
import com.coda_fofos.java_akademika.dtos.RegisterUserRequestDTO;
import com.coda_fofos.java_akademika.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pelos endpoints de autenticação (login) e registo.
 */
@RestController // Marca a classe como um Controller REST (combina @Controller e @ResponseBody)
@RequestMapping("/auth") // Define o path base para todos os endpoints neste controller como "/auth"
public class AuthController {

    @Autowired // Injeta a instância do AuthService gerenciada pelo Spring
    private AuthService authService;

    /**
     * Endpoint para autenticar um usuário.
     * Recebe os dados de login (email, senha) no corpo da requisição (POST).
     * @param loginData DTO validado contendo email e senha.
     * @return ResponseEntity contendo o DTO de resposta com email e token e status 200 OK em caso de sucesso.
     * Retorna status 401 Unauthorized ou 403 Forbidden automaticamente pelo Spring Security
     * em caso de falha (dependendo da configuração ou se a exceção for tratada).
     */
    @PostMapping("/login") // Mapeia requisições POST para "/auth/login"
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginData) {
        // @RequestBody indica que os dados vêm do corpo da requisição JSON
        // @Valid ativa a validação das anotações no DTO (ex: @NotNull, @Email)
        LoginResponseDTO response = authService.authenticateUser(loginData);
        return ResponseEntity.ok(response); // Retorna 200 OK com o corpo da resposta
    }

    /**
     * Endpoint para registar um novo usuário.
     * Recebe os dados de registo (username, password, email) no corpo da requisição (POST).
     * @param registerData DTO validado contendo os dados do novo usuário.
     * @return ResponseEntity com status 201 Created em caso de sucesso.
     * Pode retornar outros status (ex: 400 Bad Request) se a validação falhar ou
     * se o AuthService lançar uma exceção (ex: email já existe - idealmente tratada
     * globalmente para retornar 409 Conflict ou 400).
     */
    @PostMapping("/register") // Mapeia requisições POST para "/auth/register" Ok
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserRequestDTO registerData) {
        authService.registerNewUser(registerData);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // Retorna 201 Created sem corpo na resposta
    }
}
