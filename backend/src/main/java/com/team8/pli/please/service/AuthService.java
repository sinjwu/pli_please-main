package com.team8.pli.please.service;

import com.team8.pli.please.dto.AuthRequest;
import com.team8.pli.please.dto.AuthResponse;
import com.team8.pli.please.dto.RegisterRequest;
import com.team8.pli.please.dto.UserDto;
import com.team8.pli.please.entity.AuthProvider;
import com.team8.pli.please.entity.User;
import com.team8.pli.please.exception.AuthenticationException;
import com.team8.pli.please.exception.BadRequestException;
import com.team8.pli.please.exception.UserAlreadyExistsException;
import com.team8.pli.please.repository.UserRepository;
import com.team8.pli.please.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("사용자명이 이미 존재합니다");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("이메일이 이미 존재합니다");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .birthDate(request.getBirthDate())
                .provider(AuthProvider.LOCAL)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(UserDto.fromEntity(user))
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        try {
            String loginId = request.getEmail() != null ? request.getEmail() : request.getUsername();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginId,
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(loginId)
                    .or(() -> userRepository.findByUsername(loginId))
                    .orElseThrow(() -> new AuthenticationException("Authentication failed"));

            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user(UserDto.fromEntity(user))
                    .build();
        } catch (BadRequestException e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }
}
