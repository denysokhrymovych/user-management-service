package com.example.usermanagementservice.mapper;

import com.example.usermanagementservice.config.MapperConfig;
import com.example.usermanagementservice.dto.UpdateUserRequestDto;
import com.example.usermanagementservice.dto.UserRegistrationRequestDto;
import com.example.usermanagementservice.dto.UserRegistrationResponseDto;
import com.example.usermanagementservice.dto.UserResponseDto;
import com.example.usermanagementservice.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toUserRegistrationResponseDto(User user);

    UserResponseDto toUserResponseDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    User toModel(UpdateUserRequestDto requestDto);
}
