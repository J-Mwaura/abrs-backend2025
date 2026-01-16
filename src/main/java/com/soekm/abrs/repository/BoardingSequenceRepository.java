package com.soekm.abrs.repository;

import com.soekm.abrs.entity.BoardingSequence;
import com.soekm.abrs.entity.enums.BoardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardingSequenceRepository
        extends JpaRepository<BoardingSequence, Long> {

    List<BoardingSequence> findByFlightIdAndStatus(Long flightId, BoardingStatus status);
    Optional<BoardingSequence> findByFlightIdAndSequenceNumber(
            Long flightId,
            Integer sequenceNumber
    );

    List<BoardingSequence> findByFlightIdAndStatusNot(
            Long flightId,
            BoardingStatus status
    );

    @Query("""
    SELECT COALESCE(MAX(s.sequenceNumber), 0)
    FROM BoardingSequence s
    WHERE s.flight.id = :flightId
""")
    int findMaxSequenceForFlight(Long flightId);

    long countByFlightIdAndStatus(Long flightId, BoardingStatus boardingStatus);


}
