package com.soekm.abrs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * DTO for Flight entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {

    private Long id;

    private String flightNumber;
    private java.time.LocalDate flightDate;

    private String origin;       // maps from departureAirport
    private String destination;  // maps from arrivalAirport

    private ZonedDateTime departureTime;

    /**
     * 🔑 INPUT FIELD: The "Highest SEQ" from the check-in gate.
     * This is used during creation to generate the sequences 1..N.
     */
    private Integer highestSequence;
    private Integer checkedInSeats; // sequences.size()
    private Integer boardedSeats;   // sequences.stream().filter(BoardingSequence::isBoarded).count()

    private String status;          // flight.getStatus().name()

    private String notes;           // optional, if added later
}

