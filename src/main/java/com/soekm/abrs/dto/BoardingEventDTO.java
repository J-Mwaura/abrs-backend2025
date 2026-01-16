package com.soekm.abrs.dto;

import com.soekm.abrs.entity.enums.BoardingEventType;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * @author James Mwaura
 * 2026
 *
 * API-facing DTO for boarding event audit logs
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardingEventDTO {

    private Long id;

    /** Reference identifiers only (no entity exposure) */
    private Long flightId;
    private Long sequenceId;
    private Integer sequenceNumber;

    private BoardingEventType eventType;

    private ZonedDateTime eventTime;

    /** Nullable until security is enabled */
    private Long staffUserId;

    private String details;
}
