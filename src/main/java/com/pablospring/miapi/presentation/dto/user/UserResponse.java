package com.pablospring.miapi.presentation.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String username;
    private String email;
    private Set<String> roles;
}
