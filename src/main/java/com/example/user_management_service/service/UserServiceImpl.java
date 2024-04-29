package com.example.user_management_service.service;

import com.example.user_management_service.exception.RegistrationException;
import com.example.user_management_service.dto.UserRegistrationRequestDto;
import com.example.user_management_service.dto.UserRegistrationResponseDto;
import com.example.user_management_service.mapper.UserMapper;
import com.example.user_management_service.model.Role;
import com.example.user_management_service.model.User;
import com.example.user_management_service.repository.RoleRepository;
import com.example.user_management_service.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponseDto register(
            UserRegistrationRequestDto requestDto) throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Cannot register user");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        return userMapper.toDto(userRepository.save(user));
    }
}
