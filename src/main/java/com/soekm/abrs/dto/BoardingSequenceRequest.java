package com.soekm.abrs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * @author James Mwaura
 * 2026
 */

public record BoardingSequenceRequest(@NotNull @Positive
                              @Min(1)
                              @Max(999)
                              Integer sequenceNumber) {

}
