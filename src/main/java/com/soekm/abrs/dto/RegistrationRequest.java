package com.soekm.abrs.dto;

import com.soekm.abrs.entity.enums.StaffType;
import jakarta.validation.constraints.*;

/**
 * @author James Mwaura
 * 2026
 */
public record RegistrationRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Username is required")
        String username,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Phone is required")
        String phone,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Size(min = 4, max = 4, message = "PIN must be exactly 4 digits")
        String pin
) {}