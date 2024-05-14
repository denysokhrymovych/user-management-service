package com.example.usermanagementservice.service;

import com.example.usermanagementservice.dto.PatchUserRequestDto;
import com.example.usermanagementservice.dto.UpdateUserRequestDto;
import com.example.usermanagementservice.dto.UserResponseDto;
import com.example.usermanagementservice.exception.DuplicateEmailException;
import com.example.usermanagementservice.exception.EntityNotFoundException;
import com.example.usermanagementservice.mapper.UserMapper;
import com.example.usermanagementservice.model.User;
import com.example.usermanagementservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private static final String USER_NOT_FOUND = "User not found with ID: ";
    private static final String USER_REGISTERED = "User with this email is already registered";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return userMapper.toUserResponseDto(user);
        } else {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }
    }

    @Override
    public UserResponseDto updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        String currentEmail = getUserById(userId).getEmail();
        String updateEmail = updateUserRequestDto.getEmail();

        if (!currentEmail.equals(updateEmail)
                && userRepository.findByEmail(updateEmail).isPresent()) {
            throw new DuplicateEmailException(USER_REGISTERED);
        }

        User user = userMapper.toModel(updateUserRequestDto);

        user.setId(userId);
        user.setPassword(passwordEncoder.encode(updateUserRequestDto.getPassword()));
        user.setRoles(getUserById(userId).getRoles());

        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto patchUser(Long userId, PatchUserRequestDto patchUserRequestDto) {
        User user = getUserById(userId);

        String currentEmail = user.getEmail();
        String patchEmail = patchUserRequestDto.getEmail();

        if (patchEmail != null
                && !currentEmail.equals(patchEmail)
                && userRepository.findByEmail(patchEmail).isPresent()) {
            throw new DuplicateEmailException(USER_REGISTERED);
        }

        if (patchEmail != null) {
            user.setEmail(patchUserRequestDto.getEmail());
        }
        if (patchUserRequestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(patchUserRequestDto.getPassword()));
        }
        if (patchUserRequestDto.getFirstName() != null) {
            user.setFirstName(patchUserRequestDto.getFirstName());
        }
        if (patchUserRequestDto.getLastName() != null) {
            user.setLastName(patchUserRequestDto.getLastName());
        }
        if (patchUserRequestDto.getBirthDate() != null) {
            user.setBirthDate(patchUserRequestDto.getBirthDate());
        }
        if (patchUserRequestDto.getAddress() != null) {
            user.setAddress(patchUserRequestDto.getAddress());
        }
        if (patchUserRequestDto.getPhoneNumber() != null) {
            user.setPhoneNumber(patchUserRequestDto.getPhoneNumber());
        }

        User patchedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(patchedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserResponseDto> findUsersByBirthDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("\"From\" date must be less than \"To\" date.");
        }

        List<User> users = userRepository.findByBirthDateBetween(from, to);
        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).get();
    }
}
