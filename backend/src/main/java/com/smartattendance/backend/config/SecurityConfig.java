package com.smartattendance.backend.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * ==========================================================
 * Security Configuration
 * ----------------------------------------------------------
 * This class controls application security.
 *
 * Main responsibilities:
 * 1. Configure password hashing using BCrypt.
 * 2. Allow public access to authentication APIs.
 * 3. Protect all other backend APIs.
 * 4. Disable server-side login sessions.
 * 5. Configure CORS for the React frontend.
 * ==========================================================
 */

@Configuration
public class SecurityConfig {

    /**
     * ======================================================
     * Password Encoder
     * ------------------------------------------------------
     * BCrypt converts a plain password into a secure,
     * one-way password hash.
     *
     * Example:
     * Plain password:
     * Sambit@123
     *
     * Stored password:
     * $2a$10$...
     *
     * The original password cannot be recovered from the hash.
     * ======================================================
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ======================================================
     * Security Filter Chain
     * ------------------------------------------------------
     * Defines which URLs are public and which URLs require
     * authentication.
     * ======================================================
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                /*
                 * This backend will use token-based authentication.
                 *
                 * We are not using Spring's traditional HTML form
                 * login or server session authentication.
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * Enable CORS using the configuration provided
                 * in corsConfigurationSource().
                 */
                .cors(Customizer.withDefaults())

                /*
                 * JWT authentication is stateless.
                 *
                 * Spring Security will not create or use an
                 * HTTP session to remember logged-in users.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Define URL access rules.
                 */
                .authorizeHttpRequests(authorize -> authorize

                        /*
                         * Registration, login, refresh-token,
                         * and logout APIs will remain public.
                         */
                        .requestMatchers("/api/auth/**").permitAll()

                        /*
                         * Allow Spring Boot error responses.
                         */
                        .requestMatchers("/error").permitAll()

                        /*
                         * Every other request must be authenticated.
                         */
                        .anyRequest().authenticated()
                )

                /*
                 * Disable Spring Security's default login page.
                 */
                .formLogin(form -> form.disable())

                /*
                 * Disable HTTP Basic authentication popup.
                 */
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    /**
     * ======================================================
     * CORS Configuration
     * ------------------------------------------------------
     * Allows the React frontend to send requests to the
     * Spring Boot backend.
     *
     * Vite commonly runs on:
     * http://localhost:5173
     * ======================================================
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        /*
         * React frontend addresses allowed to access backend.
         */
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));

        /*
         * HTTP methods allowed from frontend.
         */
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        /*
         * Headers allowed in frontend requests.
         */
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        /*
         * Allows cookies or authorization credentials.
         *
         * This will be useful later if refresh tokens are
         * stored in HttpOnly cookies.
         */
        configuration.setAllowCredentials(true);

        /*
         * Browser may read the Authorization response header.
         */
        configuration.setExposedHeaders(List.of(
                "Authorization"
        ));

        /*
         * Cache browser CORS preflight response for one hour.
         */
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        /*
         * Apply this CORS configuration to every backend URL.
         */
        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}