package com.example.smarthealth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        FirebaseAuthenticationFilter firebaseAuthenticationFilter) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/actuator/health",
                                                                "/health",
                                                                "/health/**",
                                                                "/api/public/**" // nếu có
                                                ).permitAll()
                                                // Auth APIs: yêu cầu login, nhưng không ràng buộc role
                                                .requestMatchers("/api/auth/**").authenticated()
                                                // Admin APIs
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                // các API khác chỉ cần login
                                                .anyRequest().authenticated())
                                .addFilterBefore(
                                                firebaseAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
