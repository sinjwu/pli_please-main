package com.team8.pli.please.controller;

import com.team8.pli.please.dto.UserDto;
import com.team8.pli.please.dto.UserUpdateRequest;
import com.team8.pli.please.repository.UserRepository;
import com.team8.pli.please.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public UserDto me() {
        return UserDto.fromEntity(authenticationService.getCurrentUser());
    }

    @PutMapping("/me")
    public UserDto updateMe(@Valid @RequestBody UserUpdateRequest request) {
        var user = authenticationService.getCurrentUser();
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());

        return UserDto.fromEntity(userRepository.save(user));
    }
}
