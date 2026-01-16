package com.soekm.abrs.dto;

import java.util.List;

/**
 * @author James Mwaura
 * 2026
 */

public record MissingSequencesResponse(
        Long flightId,
        List<Integer> missingSequenceNumbers) {
}
