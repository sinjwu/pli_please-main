package com.team8.pli.please.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
