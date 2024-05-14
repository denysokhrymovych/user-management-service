package com.example.usermanagementservice.service;

import com.example.usermanagementservice.dto.UserRegistrationRequestDto;
import com.example.usermanagementservice.dto.UserRegistrationResponseDto;
import com.example.usermanagementservice.exception.RegistrationException;

public interface UserAuthService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
