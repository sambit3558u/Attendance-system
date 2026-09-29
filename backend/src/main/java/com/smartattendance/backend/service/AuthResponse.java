package com.smartattendance.backend.dto;

import com.smartattendance.backend.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private boolean success;

    private String message;

    private String accessToken;

    private Long expiresIn;

    private Long userId;

    private String name;

    private String email;

    private Role role;
}