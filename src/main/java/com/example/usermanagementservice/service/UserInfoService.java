package com.example.usermanagementservice.service;

import com.example.usermanagementservice.dto.PatchUserRequestDto;
import com.example.usermanagementservice.dto.UpdateUserRequestDto;
import com.example.usermanagementservice.dto.UserResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface UserInfoService {
    UserResponseDto getUser(Long userId);

    UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto);

    UserResponseDto patchUser(Long userId, PatchUserRequestDto patchUserRequestDto);

    void deleteUser(Long userId);

    List<UserResponseDto> findUsersByBirthDateRange(LocalDate from, LocalDate to);
}
