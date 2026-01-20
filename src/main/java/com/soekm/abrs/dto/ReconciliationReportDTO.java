package com.soekm.abrs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationReportDTO {
    private Long flightId;
    private String flightNumber;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime closedAt;

    private List<BoardingSequenceDTO> boardedPassengers;
    private List<BoardingSequenceDTO> noShowPassengers;
    private List<Integer> boardingGaps;

    private Map<String, Integer> statistics;
    private Map<String, String> summary;
    private String reportGeneratedBy;
    private LocalDateTime reportGeneratedAt;
}