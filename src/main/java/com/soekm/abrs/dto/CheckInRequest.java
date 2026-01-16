package com.soekm.abrs.dto;

/**
 * @author James Mwaura
 * 2026
 */

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CheckInRequest(
        @NotNull @Positive
        Integer sequenceNumber
) {}

