package com.pablospring.miapi.service;

import com.pablospring.miapi.config.JwtUtils;
import com.pablospring.miapi.domain.exception.BadCredentialsException;
import com.pablospring.miapi.domain.exception.ConflictException;
import com.pablospring.miapi.domain.exception.NotFoundException;
import com.pablospring.miapi.domain.model.Role;
import com.pablospring.miapi.domain.model.RoleName;
import com.pablospring.miapi.domain.model.User;
import com.pablospring.miapi.domain.repository.RoleRepository;
import com.pablospring.miapi.domain.repository.UserRepository;
import com.pablospring.miapi.presentation.dto.auth.RegisterRequest;
import com.pablospring.miapi.presentation.dto.user.UserResponse;
import com.pablospring.miapi.presentation.dto.auth.LoginRequest;
import com.pablospring.miapi.presentation.dto.auth.LoginResponse;
import jakarta.annotation.PostConstruct;
import lombok.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    @PostConstruct
    public void initRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Usuario ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Correo ya existe");
        }

        Set<Role> roles = new HashSet<>();
        for (String role : request.getRoles()) {
            RoleName roleName = RoleName.valueOf(role);
            Role roleEntity = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
            roles.add(roleEntity);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {

        // Buscar usuario por email o username
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña inválidos"));

        // Autenticar con username real (Spring usa el username)
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(user);
        String refresh = jwtUtils.generateRefreshToken(user);

        UserResponse userDto = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );

        return new LoginResponse(token, refresh, userDto);

    }
}
