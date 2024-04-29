package com.example.user_management_service.mapper;

import com.example.user_management_service.config.MapperConfig;
import com.example.user_management_service.dto.UserRegistrationRequestDto;
import com.example.user_management_service.dto.UserRegistrationResponseDto;
import com.example.user_management_service.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
