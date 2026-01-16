package com.soekm.abrs.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @author James Mwaura
 * 2026
 */

public record BoardingNoteRequest(@NotBlank
                                  String note) {
}
