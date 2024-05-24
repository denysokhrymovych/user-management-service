package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.dto.PatchUserRequestDto;
import com.example.usermanagementservice.dto.UpdateUserRequestDto;
import com.example.usermanagementservice.dto.UserResponseDto;
import com.example.usermanagementservice.model.User;
import com.example.usermanagementservice.security.UserContext;
import com.example.usermanagementservice.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User info controller", description = "Endpoints for user info")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserInfoService userInfoService;
    private final UserContext userContext;

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/{userId}")
    @Operation(summary = "Get a user", description = "Get information about a user")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return userInfoService.getUser(userId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping
    @Operation(summary = "Update a user", description = "Update user information")
    public UserResponseDto updateUser(
            @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        User user = userContext.getCurrentUser();
        return userInfoService.updateUser(user.getId(), updateUserRequestDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping
    @Operation(summary = "Patch a user", description = "Patch user information")
    public UserResponseDto patchUser(
            @Valid @RequestBody PatchUserRequestDto patchUserRequestDto) {
        User user = userContext.getCurrentUser();
        return userInfoService.patchUser(user.getId(), patchUserRequestDto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user", description = "Delete a user")
    public void deleteUser() {
        User user = userContext.getCurrentUser();
        userInfoService.deleteUser(user.getId());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/search-by-birthdate-range")
    @Operation(
            summary = "Search users by birth date range",
            description = "Search users by birth date range")
    public List<UserResponseDto> searchUsersByBirthDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return userInfoService.findUsersByBirthDateRange(from, to);
    }
}
