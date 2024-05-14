package com.example.usermanagementservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PatchUserRequestDto {
    @Size(max = 255, message = "Email cannot be greater than 255 characters")
    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 20, message = "Password cannot be greater than 20 characters")
    private String password;

    @Size(max = 255, message = "First name cannot be greater than 255 characters")
    private String firstName;

    @Size(max = 255, message = "Last name cannot be greater than 255 characters")
    private String lastName;

    @Past(message = "Birth date must be earlier than the current date")
    private LocalDate birthDate;

    @Size(max = 255, message = "Address cannot be greater than 255 characters")
    private String address;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phoneNumber;
}
