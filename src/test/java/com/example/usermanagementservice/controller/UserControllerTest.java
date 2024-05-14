package com.example.usermanagementservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.usermanagementservice.dto.PatchUserRequestDto;
import com.example.usermanagementservice.dto.UpdateUserRequestDto;
import com.example.usermanagementservice.dto.UserResponseDto;
import com.example.usermanagementservice.exception.EntityNotFoundException;
import com.example.usermanagementservice.model.User;
import com.example.usermanagementservice.repository.UserRepository;
import com.example.usermanagementservice.security.JwtUtil;
import com.example.usermanagementservice.service.UserInfoServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(UserController.class)
@MockBean(JwtUtil.class)
@MockBean(UserRepository.class)
@MockBean(UserDetailsService.class)
class UserControllerTest {
    protected static MockMvc mockMvc;
    private static final Long ID_OF_EXISTING_USER = 1L;
    private static final Long ID_OF_NONEXISTENT_USER = 10L;
    private static final Long ID_OF_USER_FOR_UPDATE = 4L;
    private static final Long ID_OF_USER_FOR_PATCH = 7L;
    private static final Long ID_OF_USER_FOR_DELETE = 8L;
    private static final LocalDate FROM = LocalDate.parse("1995-01-05");
    private static final LocalDate TO = LocalDate.parse("2001-01-05");
    private static final String USER_NOT_FOUND = "User not found with ID: ";
    private static UserResponseDto userResponseDto;
    private static UpdateUserRequestDto updateUserRequestDto;
    private static UserResponseDto updatedUserResponseDto;
    private static PatchUserRequestDto patchUserRequestDto;
    private static UserResponseDto patchedUserResponseDto;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private UserInfoServiceImpl userInfoService;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        userResponseDto = new UserResponseDto()
                .setId(1L)
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

        updatedUserResponseDto = new UserResponseDto()
                .setId(4L)
                .setEmail("example1111@example.com")
                .setFirstName("David")
                .setLastName("Smith")
                .setBirthDate(LocalDate.parse("1995-05-21"))
                .setAddress("Kyiv")
                .setPhoneNumber("4321004321");

        patchUserRequestDto = new PatchUserRequestDto()
                .setAddress("Dnipro")
                .setPhoneNumber("3535676701");

        patchedUserResponseDto = new UserResponseDto()
                .setId(7L)
                .setEmail("example2222@example.com")
                .setFirstName("Michael")
                .setLastName("Taylor")
                .setBirthDate(LocalDate.parse("1997-08-12"))
                .setAddress("Dnipro")
                .setPhoneNumber("3535676701");
    }

    @BeforeEach
    void beforeEach() {
        User user = new User()
                .setId(1L)
                .setEmail("example2222@example.com");

        when(userDetailsService.loadUserByUsername("example2222@example.com")).thenReturn(user);
    }

    @Test
    @DisplayName("Get a user by an existing ID")
    @WithMockUser(username = "user", roles = {"USER"})
    void getUser_ExistingId_Success() throws Exception {
        when(userInfoService.getUser(ID_OF_EXISTING_USER)).thenReturn(userResponseDto);

        MvcResult result = mockMvc.perform(
                        get("/users/{userId}", ID_OF_EXISTING_USER))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        assertNotNull(actual);
        assertEquals(userResponseDto, actual);
    }

    @Test
    @DisplayName("Get a user by a nonexistent ID")
    @WithMockUser(username = "user", roles = {"USER"})
    void getUser_NonexistentId_NotFound() throws Exception {
        when(userInfoService.getUser(ID_OF_NONEXISTENT_USER))
                .thenThrow(new EntityNotFoundException(USER_NOT_FOUND + ID_OF_NONEXISTENT_USER));

        MvcResult result = mockMvc.perform(
                        get("/users/{userId}", ID_OF_NONEXISTENT_USER))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Update all fields of a current user")
    @WithMockUser(username = "example1111@example.com", roles = {"USER"})
    void updateUser_ValidRequestDto_Success() throws Exception {
        when(userInfoService.updateUser(ID_OF_USER_FOR_UPDATE, updateUserRequestDto))
                .thenReturn(updatedUserResponseDto);

        String jsonRequest = objectMapper.writeValueAsString(updateUserRequestDto);
        MvcResult result = mockMvc.perform(
                        put("/users")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(updatedUserResponseDto, actual);
    }

    @Test
    @DisplayName("Patch a current user")
    @WithUserDetails(value = "example2222@example.com")
    void patchUser_ValidRequestDto_Success() throws Exception {

        when(userInfoService.patchUser(ID_OF_USER_FOR_PATCH, patchUserRequestDto))
                .thenReturn(patchedUserResponseDto);

        String jsonRequest = objectMapper.writeValueAsString(patchUserRequestDto);
        MvcResult result = mockMvc.perform(
                        patch("/users")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(patchedUserResponseDto, actual);
    }

    @Test
    @DisplayName("Delete a current user")
    @WithMockUser(username = "example4444@example.com")
    void deleteUser_ExistingUser_NoContent() throws Exception {
        doNothing().when(userInfoService).deleteUser(ID_OF_USER_FOR_DELETE);

        mockMvc.perform(
                        delete("/users")
                )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @DisplayName("Search users by a valid birth date range")
    @WithMockUser(username = "example5555@example.com")
    void searchUsersByBirthDateRange_ValidBirthDateRange_Success() throws Exception {
        when(userInfoService.findUsersByBirthDateRange(FROM, TO))
                .thenReturn(List.of(new UserResponseDto(), new UserResponseDto(),
                        new UserResponseDto(), new UserResponseDto()));
        int expectedSize = 4;

        MvcResult result = mockMvc.perform(
                        get("/users/search-by-birthdate-range")
                                .param("from", String.valueOf(FROM))
                                .param("to", String.valueOf(TO))
                )
                .andExpect(status().isOk())
                .andReturn();

        List<UserResponseDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {}
        );

        assertEquals(expectedSize, actual.size());
    }

    @Test
    @DisplayName("Search users by an invalid birth date range")
    @WithMockUser(username = "example7777@example.com", roles = {"USER"})
    void searchUsersByBirthDateRange_InvalidBirthDateRange_BadRequest() throws Exception {
        when(userInfoService.findUsersByBirthDateRange(TO, FROM)).thenThrow(
                new IllegalArgumentException("\"From\" date must be less than \"To\" date."));

        MvcResult result = mockMvc.perform(
                        get("/users/search-by-birthdate-range")
                                .param("from", String.valueOf(TO))
                                .param("to", String.valueOf(FROM))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
