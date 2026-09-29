package com.smartattendance.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.smartattendance.backend.entity.Role;
import com.smartattendance.backend.entity.User;
import com.smartattendance.backend.repository.UserRepository;

@Configuration
public class AdminInitializer {
    @Bean
    CommandLineRunner createDefaultAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByEmail("admin@smartattendance.com").isEmpty()) {
                users.save(User.builder()
                        .name("System Administrator")
                        .email("admin@smartattendance.com")
                        .phone("9999999999")
                        .password(encoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .department("Administration")
                        .build());
            }
        };
    }
}
