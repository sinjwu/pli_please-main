package com.team8.pli.please.dto;

import com.team8.pli.please.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private LocalDate birthDate;
    private String bio;
    private String profileImageUrl;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .birthDate(user.getBirthDate())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
