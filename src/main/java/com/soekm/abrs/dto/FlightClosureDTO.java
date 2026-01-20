package com.soekm.abrs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightClosureDTO {
    private Long flightId;
    private String flightNumber;
    private LocalDateTime closedAt;
    private String closedBy;
    private String closureReason;
    private int totalPassengers;
    private int boardedCount;
    private int noShowCount;
    private boolean isClosed;
    private String message;
}