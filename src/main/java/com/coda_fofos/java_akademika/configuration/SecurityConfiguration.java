package com.coda_fofos.java_akademika.configuration;

// IMPORTES NOVOS
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
// FIM IMPORTES NOVOS

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

// ADICIONE ESTE IMPORTE NOVO
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${api.security.token.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                // Sua configuração de headers já permite o H2 Console (pois desabilita a proteção de frames)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(authorize -> authorize

                        // ADICIONE ESTA LINHA PARA LIBERAR O H2-CONSOLE
                        .requestMatchers(PathRequest.toH2Console()).permitAll()

                        // Suas regras existentes
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretKeyBytes = jwtSecret.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "HMACSHA256");

        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    // O resto do seu código (JwtEncoder removido, etc.) está correto.
}