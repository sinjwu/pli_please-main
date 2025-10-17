package com.team8.pli.please.service;

import com.team8.pli.please.entity.User;
import com.team8.pli.please.exception.ResourceNotFoundException;
import com.team8.pli.please.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResourceNotFoundException("인증된 사용자를 찾을 수 없습니다");
        }

        String username;

        if (authentication.getPrincipal() instanceof User userPrincipal) {
            username = userPrincipal.getUsername();
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = authentication.getName();
        }

        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자명인 사용자를 찾을 수 없습니다: " + username));
    }
}
