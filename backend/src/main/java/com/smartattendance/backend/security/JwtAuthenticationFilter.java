package com.smartattendance.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * Authorization header example:
         * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         */
        final String authorizationHeader =
                request.getHeader("Authorization");

        /*
         * Agar Authorization header hi nahi hai
         * ya Bearer token format me nahi hai,
         * request ko normally continue karne do.
         */
        if (
                authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * "Bearer " ke baad actual JWT token hota hai.
         */
        final String token =
                authorizationHeader.substring(7);

        try {

            /*
             * JWT token se email extract karo.
             */
            final String email =
                    jwtService.extractEmail(token);

            /*
             * Agar email mila hai aur user already
             * authenticate nahi hua hai tabhi authentication set karo.
             */
            if (
                    email != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null
            ) {

                User user = userRepository
                        .findByEmail(email.toLowerCase())
                        .orElse(null);

                /*
                 * User database me exist karta hai
                 * aur token valid hai to Spring Security
                 * authentication create karo.
                 */
                if (
                        user != null &&
                        jwtService.isTokenValid(token, user)
                ) {

                    /*
                     * Spring Security roles ko generally
                     * ROLE_ prefix ke saath expect karta hai.
                     *
                     * TEACHER -> ROLE_TEACHER
                     * STUDENT -> ROLE_STUDENT
                     */
                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority(
                                    "ROLE_" + user.getRole().name()
                            );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(authority)
                            );

                    /*
                     * Authentication ko SecurityContext me set karo.
                     * Ab controller/service authenticated user access
                     * kar sakta hai.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception exception) {

            /*
             * Invalid / expired JWT ki wajah se backend crash
             * nahi hona chahiye.
             *
             * Authentication simply set nahi hogi.
             * Protected endpoint ko Spring Security 401/403 karega.
             */
            SecurityContextHolder.clearContext();
        }

        /*
         * Request ko next filter/controller tak bhejo.
         */
        filterChain.doFilter(request, response);
    }
}