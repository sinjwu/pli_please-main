package com.team8.pli.please.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private String bio;

    private String accessToken;
    private String refreshToken;
}
