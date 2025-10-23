package com.coda_fofos.java_akademika.services;

import com.coda_fofos.java_akademika.dtos.LoginRequestDTO;
import com.coda_fofos.java_akademika.dtos.LoginResponseDTO;
import com.coda_fofos.java_akademika.dtos.RegisterUserRequestDTO;
import com.coda_fofos.java_akademika.repositories.UserRepository;
import enities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para garantir a atomicidade do registo

/**
 * Serviço responsável pela lógica de autenticação e registo de usuários.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService; // Agora o Java deve encontrar esta classe

    // Opcional: Injetar AuthenticationManager se preferir delegar a autenticação
    // @Autowired
    // private AuthenticationManager authenticationManager;

    /**
     * Autentica um usuário com base no email e senha.
     * ... (resto do método como antes)
     */
    public LoginResponseDTO authenticateUser(LoginRequestDTO loginData) {
        User user = userRepository.findByEmail(loginData.email())
                .orElseThrow(() -> new BadCredentialsException("Email não encontrado ou senha inválida."));

        if (!passwordEncoder.matches(loginData.password(), user.getPassword())) {
            throw new BadCredentialsException("Email não encontrado ou senha inválida.");
        }

        String token = tokenService.generateToken(user); // Usa o TokenService importado

        return new LoginResponseDTO(user.getEmail(), token);
    }

    /**
     * Regista um novo usuário no sistema.
     * ... (resto do método como antes)
     */
    @Transactional
    public void registerNewUser(RegisterUserRequestDTO registerData) {
        if (userRepository.findByEmail(registerData.email()).isPresent()) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }

        String encodedPassword = passwordEncoder.encode(registerData.password());
        User newUser = new User(registerData.username(), encodedPassword, registerData.email());
        userRepository.save(newUser);
    }
}