package com.example.usermanagementservice.dto;

import com.example.usermanagementservice.validation.PasswordFieldsMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@PasswordFieldsMatch
public class UpdateUserRequestDto {
    @NotBlank(message = "Email cannot be null or blank")
    @Size(max = 255, message = "Email cannot be greater than 255 characters")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be null or blank")
    @Size(max = 20, message = "Password cannot be greater than 20 characters")
    private String password;

    @NotBlank(message = "Repeated password cannot be null or blank")
    @Size(max = 20, message = "Repeated password cannot be greater than 20 characters")
    private String repeatPassword;

    @NotBlank(message = "First name cannot be null or blank")
    @Size(max = 255, message = "First name cannot be greater than 255 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be null or blank")
    @Size(max = 255, message = "Last name cannot be greater than 255 characters")
    private String lastName;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be earlier than the current date")
    private LocalDate birthDate;

    @Size(max = 255, message = "Address cannot be greater than 255 characters")
    private String address;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;
}
