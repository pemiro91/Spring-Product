package com.pablospring.miapi.presentation.dto.auth;

import com.pablospring.miapi.presentation.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String refresh_token;
    private UserResponse user;
}
