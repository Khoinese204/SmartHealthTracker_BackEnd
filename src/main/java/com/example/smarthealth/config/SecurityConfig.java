package com.example.smarthealth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler; // 403
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; // 401

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // -------------------------------
                // 0. Disable CSRF, enable stateless mode
                // -------------------------------
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // -------------------------------
                // 1. Authorization Rules
                // -------------------------------
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC → No token needed
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/api/health/**",
                                "/health/**",
                                "/api/public/**")
                        .permitAll()

                        // /api/admin/** → ADMIN only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // /api/health/** → USER only
                        .requestMatchers("/api/health/**")
                        .hasRole("USER")

                        // /api/auth/** → USER + ADMIN (just authenticated)
                        .requestMatchers("/api/auth/**").authenticated()

                        // Remaining APIs → require login
                        .anyRequest().authenticated())

                // -------------------------------
                // 2. Custom 401 and 403 Handlers
                // -------------------------------
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 401 JSON
                        .accessDeniedHandler(customAccessDeniedHandler) // 403 JSON
                )

                // -------------------------------
                // 3. Firebase Authentication Filter
                // -------------------------------
                .addFilterBefore(
                        firebaseAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
