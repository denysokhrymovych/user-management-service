package com.example.user_management_service.service;

import com.example.user_management_service.dto.UserRegistrationRequestDto;
import com.example.user_management_service.dto.UserRegistrationResponseDto;
import com.example.user_management_service.exception.RegistrationException;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
