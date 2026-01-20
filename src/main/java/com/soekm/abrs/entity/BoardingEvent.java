package com.soekm.abrs.entity;

import com.soekm.abrs.entity.enums.BoardingEventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

/**
 * @author James Mwaura
 * 2026
 */

@Entity
@Table(name = "boarding_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequence_id", nullable = false)
    private BoardingSequence sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private BoardingEventType eventType;

    @CreationTimestamp
    @Column(name = "event_time", nullable = false, updatable = false)
    private ZonedDateTime eventTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_user_id")
    private AppUser staffUser; // nullable for now

    @Column(name = "details")
    private String details;
}

