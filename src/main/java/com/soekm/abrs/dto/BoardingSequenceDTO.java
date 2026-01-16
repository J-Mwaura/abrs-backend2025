package com.soekm.abrs.dto;

import com.soekm.abrs.entity.enums.BoardingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for BoardingSequence entity.
 * Provides a simplified view of each passenger sequence for the mobile app.
 *
 * @author James Mwaura
 * 2026
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardingSequenceDTO {

    private Long id;

    private Long flightId;           // maps from flight.id

    private Integer sequenceNumber;  // e.g., 42, 43, 44

    private String status;           // maps from BoardingStatus.name(), e.g., "EXPECTED", "BOARDED", "MISSING"

    private String note;             // e.g., "No-show", "Late arrival"

    private boolean boarded;         // convenience field, true if status == BOARDED
}
