package com.soekm.abrs.repository;

import com.soekm.abrs.entity.BoardingSequence;
import com.soekm.abrs.entity.enums.BoardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author James Mwaura
 * 2026
 */

public interface BoardingRepository extends JpaRepository<BoardingSequence, Long> {

    /**
     * Finds a specific sequence for a flight.
     * Used when the staff enters a number to see if it was already processed.
     */
    Optional<BoardingSequence> findByFlightIdAndSequenceNumber(Long flightId, Integer sequenceNumber);

    /**
     * Lists all manual entries for a specific flight.
     * Useful for showing the "Boarded" list on the UI.
     */
    List<BoardingSequence> findByFlightIdOrderBySequenceNumberAsc(Long flightId);

    /**
     * Counts how many passengers have successfully boarded.
     */
    long countByFlightIdAndStatus(Long flightId, BoardingStatus status);

    /**
     * Custom query to find "Missing" sequences.
     * This compares entered sequences against the max sequence provided by check-in.
     * Useful for the final boarding report.
     */
    @Query("SELECT b FROM BoardingSequence b WHERE b.flight.id = :flightId AND b.status = :status")
    List<BoardingSequence> findByFlightAndStatus(@Param("flightId") Long flightId, @Param("status") BoardingStatus status);
}
