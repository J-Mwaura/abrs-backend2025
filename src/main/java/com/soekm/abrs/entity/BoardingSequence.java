package com.soekm.abrs.entity;


import com.soekm.abrs.entity.enums.BoardingStatus;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author James Mwaura
 * 2026
 */

@Entity
@Table(
        name = "boarding_sequence",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flight_sequence",
                        columnNames = {"flight_id", "sequence_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardingSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber; // e.g. 042

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BoardingStatus status = BoardingStatus.EXPECTED;

    @Column(name = "note")
    private String note; // "No-show", "Late arrival", etc.
}

