package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.dto.UserLoginRequestDto;
import com.example.usermanagementservice.dto.UserLoginResponseDto;
import com.example.usermanagementservice.dto.UserRegistrationRequestDto;
import com.example.usermanagementservice.dto.UserRegistrationResponseDto;
import com.example.usermanagementservice.exception.RegistrationException;
import com.example.usermanagementservice.security.AuthenticationService;
import com.example.usermanagementservice.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication controller", description = "Endpoints for user authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserAuthService userAuthService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login to the app", description = "Login to the app")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/registration")
    @Operation(summary = "Register to the app", description = "Register to the app")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userAuthService.register(requestDto);
    }
}
