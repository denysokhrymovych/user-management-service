package com.example.usermanagementservice.service;

import com.example.usermanagementservice.dto.UserRegistrationRequestDto;
import com.example.usermanagementservice.dto.UserRegistrationResponseDto;
import com.example.usermanagementservice.exception.RegistrationException;
import com.example.usermanagementservice.mapper.UserMapper;
import com.example.usermanagementservice.model.Role;
import com.example.usermanagementservice.model.User;
import com.example.usermanagementservice.repository.RoleRepository;
import com.example.usermanagementservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${minimum.age}")
    private int minimumAge;

    @Override
    public UserRegistrationResponseDto register(
            UserRegistrationRequestDto requestDto) throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with this email is already registered");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = requestDto.getBirthDate();

        int age = currentDate.getYear() - birthDate.getYear();

        if (birthDate.plusYears(age).isAfter(currentDate)) {
            age--;
        }
        if (age < minimumAge) {
            throw new RegistrationException("Users must be at least " + minimumAge + " years old.");
        }

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findByRoleName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        return userMapper.toUserRegistrationResponseDto(userRepository.save(user));
    }
}
