package com.example.usermanagementservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
    private static final Long ID_OF_USER_FOR_PATCH = 11L;
    private static final Long ID_OF_USER_FOR_PATCH_WITH_DUPLICATE_EMAIL = 11L;
    private static final LocalDate FROM = LocalDate.parse("1989-01-05");
    private static final LocalDate TO = LocalDate.parse("2001-01-05");
    private static User user;
    private static UserResponseDto userResponseDto;
    private static UpdateUserRequestDto updateUserRequestDto;
    private static User userToBeUpdated;
    private static User userModelFromUpdateDto;
    private static User updatedUser;
    private static UserResponseDto updatedUserResponseDto;
    private static UpdateUserRequestDto updateUserRequestDtoWithDuplicateEmail;
    private static User userToBePatched;
    private static User patchedUser;
    private static PatchUserRequestDto patchUserRequestDto;
    private static UserResponseDto patchedUserResponseDto;
    private static PatchUserRequestDto patchUserRequestDtoWithDuplicateEmail;
    private static User user2;
    private static UserResponseDto userResponseDto2;

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

        userToBePatched = new User()
                .setId(ID_OF_USER_FOR_PATCH)
                .setEmail("example1111@example.com")
                .setPassword("$2a$10$FIx9jqvWv.FAKTmVM4A7Ru/Jb3275kln/RAfDFpQEE.pX4UCWsnfS")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Sumy")
                .setPhoneNumber("5656878734")
                .setRoles(Set.of());

        patchedUser = new User()
                .setId(ID_OF_USER_FOR_PATCH)
                .setEmail("example1111@example.com")
                .setPassword("$2a$10$FIx9jqvWv.FAKTmVM4A7Ru/Jb3275kln/RAfDFpQEE.pX4UCWsnfS")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Poltava")
                .setPhoneNumber("9876543210")
                .setRoles(Set.of());

        patchUserRequestDto = new PatchUserRequestDto()
                .setAddress("Poltava")
                .setPhoneNumber("9876543210");

        patchedUserResponseDto = new UserResponseDto()
                .setId(ID_OF_USER_FOR_PATCH)
                .setEmail("example1111@example.com")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Poltava")
                .setPhoneNumber("9876543210");

        patchUserRequestDtoWithDuplicateEmail = new PatchUserRequestDto()
                .setEmail(RESERVED_EMAIL)
                .setAddress("Poltava")
                .setPhoneNumber("9876543210");

        user2 = new User()
                .setId(3L)
                .setEmail("example4400@example.com")
                .setPassword("$2a$10$nqiHTVWKSLdqo6e7tTmuv.lK0/d8Fd3YAFw1C4H3Gelcywoy5iAb.")
                .setFirstName("William")
                .setLastName("Garcia")
                .setBirthDate(LocalDate.parse("2000-12-02"))
                .setAddress("Vinnytsia")
                .setPhoneNumber("4362639578");

        userResponseDto2 = new UserResponseDto()
                .setId(3L)
                .setEmail("example4400@example.com")
                .setFirstName("William")
                .setLastName("Garcia")
                .setBirthDate(LocalDate.parse("2000-12-02"))
                .setAddress("Vinnytsia")
                .setPhoneNumber("4362639578");
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

    @Test
    @DisplayName("Patch the address and phone number of a user")
    void patchUser_ValidRequestDto_ReturnsUserResponseDto() {
        when(userRepository.findById(ID_OF_USER_FOR_PATCH))
                .thenReturn(Optional.of(userToBePatched));
        when(userRepository.save(patchedUser))
                .thenReturn(patchedUser);
        when(userMapper.toUserResponseDto(patchedUser))
                .thenReturn(patchedUserResponseDto);

        UserResponseDto expected = patchedUserResponseDto;
        UserResponseDto actual
                = userInfoService.patchUser(ID_OF_USER_FOR_PATCH, patchUserRequestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Patch a user's email with the email of another user")
    void patchUser_DuplicateEmail_ThrowsDuplicateEmailException() {
        when(userRepository.findById(ID_OF_USER_FOR_PATCH_WITH_DUPLICATE_EMAIL))
                .thenReturn(Optional.of(userToBePatched));
        when(userRepository.findByEmail(RESERVED_EMAIL))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(
                DuplicateEmailException.class,
                () -> userInfoService.patchUser(
                        ID_OF_USER_FOR_PATCH_WITH_DUPLICATE_EMAIL,
                        patchUserRequestDtoWithDuplicateEmail)
        );

        String expected = "User with this email is already registered";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find users between FROM and TO dates")
    void findUsersByBirthDateRange_ValidBirthDateRange_ReturnsUserResponseDtoList() {
        when(userRepository.findByBirthDateBetween(FROM, TO))
                .thenReturn(List.of(user, user2));
        when(userMapper.toUserResponseDto(user))
                .thenReturn(userResponseDto);
        when(userMapper.toUserResponseDto(user2))
                .thenReturn(userResponseDto2);

        List<UserResponseDto> expected = List.of(userResponseDto, userResponseDto2);
        List<UserResponseDto> actual = userInfoService.findUsersByBirthDateRange(FROM, TO);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find users between TO and FROM dates")
    void findUsersByBirthDateRange_FromAndToDatesInverted_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> userInfoService.findUsersByBirthDateRange(TO, FROM)
        );

        String expected = "\"From\" date must be less than \"To\" date.";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }
}
