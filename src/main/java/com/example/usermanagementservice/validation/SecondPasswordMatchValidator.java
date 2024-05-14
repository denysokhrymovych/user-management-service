package com.example.usermanagementservice.validation;

import com.example.usermanagementservice.dto.UpdateUserRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class SecondPasswordMatchValidator
        implements ConstraintValidator<PasswordFieldsMatch, UpdateUserRequestDto> {
    @Override
    public void initialize(PasswordFieldsMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(
            UpdateUserRequestDto userDto,
            ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(userDto.getPassword(), userDto.getRepeatPassword());
    }
}
