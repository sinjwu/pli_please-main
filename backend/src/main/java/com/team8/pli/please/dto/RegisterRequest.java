package com.team8.pli.please.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank(message = "사용자명은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자명은 3~50 글자 사이여야 합니다")
    private String username;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일을 입력해야 합니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 10, message = "비밀번호는 최소 10자리 이상이어야 합니다")
    private String password;

    @NotNull(message = "생년월일은 필수입니다")
    @Past
    private LocalDate birthDate;

    @NotBlank(message = "풀 네임 입력은 필수입니다")
    private String fullName;
}
