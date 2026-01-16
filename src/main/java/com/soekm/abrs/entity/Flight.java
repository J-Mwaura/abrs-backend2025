package com.soekm.abrs.entity;


import com.soekm.abrs.entity.enums.FlightStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author James Mwaura
 * 2026
 */

@Entity
@Table(
        name = "flight",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flight_number_date",
                        columnNames = {"flight_number", "flight_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber; // e.g. EK202

    @Column(name = "flight_date", nullable = false)
    private LocalDate flightDate;

    @Column(name = "departure_airport", nullable = true)
    private String departureAirport; // e.g. NBO

    @Column(name = "arrival_airport", nullable = true)
    private String arrivalAirport; // e.g. DXB

    @Column(name = "departure_time", nullable = true)
    private ZonedDateTime departureTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FlightStatus status = FlightStatus.CREATED;

    @OneToMany(
            mappedBy = "flight",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private Set<BoardingSequence> sequences = new HashSet<>();
}

