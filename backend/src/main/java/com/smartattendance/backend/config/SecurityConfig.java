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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.smartattendance.backend.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // =====================================================
    // JWT FILTER
    // =====================================================

    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(
                        csrf ->
                                csrf.disable()
                )

                // =================================================
                // CORS
                // =================================================

                .cors(
                        Customizer.withDefaults()
                )

                // =================================================
                // SESSION
                // =================================================

                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(
                        authorize ->
                                authorize

                                        // =================================
                                        // PUBLIC
                                        // =================================

                                        .requestMatchers(
                                                "/",
                                                "/error"
                                        )
                                        .permitAll()

                                        // =================================
                                        // AUTH
                                        // =================================

                                        .requestMatchers(
                                                "/api/auth/**"
                                        )
                                        .permitAll()

                                        // Public read-only catalog for registration.
                                        // Academic structure changes remain admin-only.
                                        .requestMatchers(
                                                "/api/academic/**"
                                        )
                                        .permitAll()

                                        // =================================
                                        // SUBJECTS
                                        //
                                        // Temporary public because
                                        // existing frontend may use them
                                        // directly.
                                        // =================================

                                        .requestMatchers(
                                                "/api/subjects",
                                                "/api/subjects/**"
                                        )
                                        .permitAll()

                                        // =================================
                                        // TIMETABLES
                                        //
                                        // Temporary public because
                                        // existing frontend may use them.
                                        // =================================

                                        .requestMatchers(
                                                "/api/timetables",
                                                "/api/timetables/**"
                                        )
                                        .permitAll()

                                        // =================================
                                        // ADMIN
                                        //
                                        // Includes:
                                        //
                                        // /api/admin/dashboard
                                        // /api/admin/users
                                        // /api/admin/locations
                                        // etc.
                                        // =================================

                                        .requestMatchers(
                                                "/api/admin/**"
                                        )
                                        .hasRole("ADMIN")

                                        // =================================
                                        // TEACHER
                                        //
                                        // Includes:
                                        //
                                        // /api/teacher/dashboard
                                        // /api/teacher/attendance/mark
                                        // /api/teacher/classes
                                        // etc.
                                        // =================================

                                        .requestMatchers(
                                                "/api/teacher/**"
                                        )
                                        .hasRole("TEACHER")

                                        // =================================
                                        // STUDENT
                                        // =================================

                                        .requestMatchers(
                                                "/api/student/**"
                                        )
                                        .hasRole("STUDENT")

                                        // =================================
                                        // CLASS SESSION
                                        //
                                        // Existing class session controller
                                        // may currently be used by teacher.
                                        // Keep authenticated for now.
                                        // =================================

                                        .requestMatchers(
                                                "/api/classes/**",
                                                "/api/class-sessions/**"
                                        )
                                        .authenticated()

                                        // =================================
                                        // EVERYTHING ELSE
                                        // =================================

                                        .anyRequest()
                                        .authenticated()
                )

                // =================================================
                // FORM LOGIN OFF
                // =================================================

                .formLogin(
                        form ->
                                form.disable()
                )

                // =================================================
                // HTTP BASIC OFF
                // =================================================

                .httpBasic(
                        httpBasic ->
                                httpBasic.disable()
                )

                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =====================================================
    // CORS CONFIGURATION
    // =====================================================

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // =================================================
        // FRONTEND ORIGINS
        // =================================================

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://192.168.1.10:5173"
                )
        );

        // =================================================
        // HTTP METHODS
        // =================================================

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // =================================================
        // REQUEST HEADERS
        // =================================================

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin"
                )
        );

        // =================================================
        // CREDENTIALS
        // =================================================

        configuration.setAllowCredentials(
                true
        );

        // =================================================
        // EXPOSED HEADERS
        // =================================================

        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );

        // =================================================
        // PREFLIGHT CACHE
        // =================================================

        configuration.setMaxAge(
                3600L
        );

        // =================================================
        // APPLY CORS
        // =================================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
