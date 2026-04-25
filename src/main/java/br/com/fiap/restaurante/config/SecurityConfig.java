package br.com.fiap.restaurante.config;

import br.com.fiap.restaurante.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança — JWT Stateless.
 *
 * <p>Rotas públicas:
 * <ul>
 *   <li>POST /auth/login  — autenticação e geração do token</li>
 *   <li>POST /api/v1/usuarios — cadastro de novo usuário</li>
 *   <li>/swagger-ui/**   — documentação OpenAPI</li>
 *   <li>/v3/api-docs/**  — documentação OpenAPI</li>
 * </ul>
 *
 * <p>Todas as demais rotas exigem token JWT válido no header
 * {@code Authorization: Bearer <token>}.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (API REST stateless não usa cookies de sessão)
                .csrf(AbstractHttpConfigurer::disable)

                // Sem sessão HTTP — cada request é autenticado via token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Qualquer outro endpoint exige autenticação
                        .anyRequest().authenticated()
                )

                // Registra o filtro JWT antes do filtro padrão de usuário/senha
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}