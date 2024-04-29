package com.example.user_management_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank(message = "Email cannot be null or blank")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password cannot be null or blank")
        @Size(max = 20, message = "Password cannot be greater than 20 characters")
        String password
) {
}
