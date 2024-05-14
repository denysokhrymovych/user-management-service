package com.example.usermanagementservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.usermanagementservice.dto.UpdateUserRequestDto;
import com.example.usermanagementservice.dto.UserResponseDto;
import com.example.usermanagementservice.exception.DuplicateEmailException;
import com.example.usermanagementservice.exception.EntityNotFoundException;
import com.example.usermanagementservice.mapper.UserMapper;
import com.example.usermanagementservice.model.User;
import com.example.usermanagementservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceImplTest {
    private static final Long ID_OF_EXISTING_USER = 2L;
    private static final Long ID_OF_NONEXISTENT_USER = 15L;
    private static final Long ID_OF_USER_FOR_UPDATE = 11L;
    private static final String ENCRYPTED_PASSWORD
            = "$2a$10$aatOGGMyH/ni2OcIHtR0A.J3m814SzQ9nJa0vkawqTSRqIhGyE/Oq";
    private static final Long ID_OF_USER_FOR_UPDATE_WITH_DUPLICATE_EMAIL = 11L;
    private static final String RESERVED_EMAIL = "example1234@example.com";
    private static User user;
    private static UserResponseDto userResponseDto;
    private static UpdateUserRequestDto updateUserRequestDto;
    private static User userToBeUpdated;
    private static User userModelFromUpdateDto;
    private static User updatedUser;
    private static UserResponseDto updatedUserResponseDto;
    private static UpdateUserRequestDto updateUserRequestDtoWithDuplicateEmail;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserInfoServiceImpl userInfoService;

    @BeforeAll
    static void beforeAll() {
        user = new User()
                .setId(ID_OF_EXISTING_USER)
                .setEmail("example1234@example.com")
                .setPassword("$2a$10$8gz3T2DmwpOaaKEGhYTkqupwz9jsv3.x68mdbksYPSPNBTpbqnlCm")
                .setFirstName("Alice")
                .setLastName("Johnson")
                .setBirthDate(LocalDate.parse("1990-02-01"))
                .setAddress("Lviv")
                .setPhoneNumber("1234123400");

        userResponseDto = new UserResponseDto()
                .setId(ID_OF_EXISTING_USER)
                .setEmail("example1234@example.com")
                .setFirstName("Alice")
                .setLastName("Johnson")
                .setBirthDate(LocalDate.parse("1990-02-01"))
                .setAddress("Lviv")
                .setPhoneNumber("1234123400");

        updateUserRequestDto = new UpdateUserRequestDto()
                .setEmail("example1111@example.com")
                .setPassword("123123123")
                .setRepeatPassword("123123123")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Kyiv")
                .setPhoneNumber("4321004321");

        userToBeUpdated = new User()
                .setId(ID_OF_USER_FOR_UPDATE)
                .setEmail("example1111@example.com")
                .setPassword("$2a$10$FIx9jqvWv.FAKTmVM4A7Ru/Jb3275kln/RAfDFpQEE.pX4UCWsnfS")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Sumy")
                .setPhoneNumber("5656878734")
                .setRoles(Set.of());

        userModelFromUpdateDto = new User()
                .setEmail("example1111@example.com")
                .setPassword("123123123")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Kyiv")
                .setPhoneNumber("4321004321");

        updatedUser = new User()
                .setId(ID_OF_USER_FOR_UPDATE)
                .setEmail("example1111@example.com")
                .setPassword(ENCRYPTED_PASSWORD)
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Kyiv")
                .setPhoneNumber("4321004321")
                .setRoles(Set.of());

        updatedUserResponseDto = new UserResponseDto()
                .setId(ID_OF_USER_FOR_UPDATE)
                .setEmail("example1111@example.com")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Kyiv")
                .setPhoneNumber("4321004321");

        updateUserRequestDtoWithDuplicateEmail = new UpdateUserRequestDto()
                .setEmail(RESERVED_EMAIL)
                .setPassword("123123123")
                .setRepeatPassword("123123123")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Kyiv")
                .setPhoneNumber("4321004321");
    }

    @Test
    @DisplayName("Get a user by an existing ID")
    void getUser_ExistingId_ReturnsUserResponseDto() {
        when(userRepository.findById(ID_OF_EXISTING_USER)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto expected = userResponseDto;
        UserResponseDto actual = userInfoService.getUser(ID_OF_EXISTING_USER);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get a user by a nonexistent ID")
    void getUser_NonexistentId_ThrowsEntityNotFoundException() {
        when(userRepository.findById(ID_OF_NONEXISTENT_USER)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> userInfoService.getUser(ID_OF_NONEXISTENT_USER)
        );

        String expected = "User not found with ID: " + ID_OF_NONEXISTENT_USER;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update all fields of a current user")
    void updateUser_ValidRequestDto_ReturnsUserResponseDto() {
        when(userRepository.findById(ID_OF_USER_FOR_UPDATE))
                .thenReturn(Optional.of(userToBeUpdated));
        when(userMapper.toModel(updateUserRequestDto))
                .thenReturn(userModelFromUpdateDto);
        when(passwordEncoder.encode(updateUserRequestDto.getPassword()))
                .thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.save(updatedUser))
                .thenReturn(updatedUser);
        when(userMapper.toUserResponseDto(updatedUser))
                .thenReturn(updatedUserResponseDto);

        UserResponseDto expected = updatedUserResponseDto;
        UserResponseDto actual
                = userInfoService.updateUser(ID_OF_USER_FOR_UPDATE, updateUserRequestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update a user's email with the email of another user")
    void updateUser_DuplicateEmail_ThrowsDuplicateEmailException() {
        when(userRepository.findById(ID_OF_USER_FOR_UPDATE_WITH_DUPLICATE_EMAIL))
                .thenReturn(Optional.of(userToBeUpdated));
        when(userRepository.findByEmail(RESERVED_EMAIL))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                DuplicateEmailException.class,
                () -> userInfoService.updateUser(
                        ID_OF_USER_FOR_UPDATE_WITH_DUPLICATE_EMAIL,
                        updateUserRequestDtoWithDuplicateEmail)
        );

        String expected = "User with this email is already registered";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }
}
