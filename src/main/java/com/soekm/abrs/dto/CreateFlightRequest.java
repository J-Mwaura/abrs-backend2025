package com.soekm.abrs.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateFlightRequest(
        @NotBlank String flightNumber,
        @NotBlank String destination,
        @NotNull LocalDateTime departureTime
) {}
