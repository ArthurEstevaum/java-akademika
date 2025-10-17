package com.coda_fofos.java_akademika.controllers;

import com.coda_fofos.java_akademika.dtos.LoginRequestDTO;
import com.coda_fofos.java_akademika.dtos.LoginResponseDTO;
import com.coda_fofos.java_akademika.dtos.UserRegistrationDTO;
import com.coda_fofos.java_akademika.entities.User;
import com.coda_fofos.java_akademika.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        Instant now = Instant.now();
        long expiry = 36000L; // 10 hours

        String scope = auth.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(loginRequestDTO.email())
                .claim("scope", scope)
                .build();

        String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseDTO(loginRequestDTO.email(), token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        if (userRepository.findByEmail(userRegistrationDTO.email()) != null) {
            return ResponseEntity.badRequest().body("Email já está em uso.");
        }

        String encryptedPassword = bCryptPasswordEncoder.encode(userRegistrationDTO.password());
        User newUser = new User(userRegistrationDTO.username(), encryptedPassword, userRegistrationDTO.email());

        userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
