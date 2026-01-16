package com.soekm.abrs.service;

import com.soekm.abrs.dto.BoardingSequenceDTO;
import com.soekm.abrs.dto.mapper.BoardingSequenceMapper;
import com.soekm.abrs.entity.BoardingEvent;
import com.soekm.abrs.entity.BoardingSequence;
import com.soekm.abrs.entity.Flight;
import com.soekm.abrs.entity.enums.BoardingEventType;
import com.soekm.abrs.entity.enums.BoardingStatus;
import com.soekm.abrs.repository.BoardingEventRepository;
import com.soekm.abrs.repository.BoardingSequenceRepository;
import com.soekm.abrs.repository.FlightRepository;
import com.soekm.abrs.service.iService.IBoardingSequenceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author James Mwaura
 * 2026
 */

@Service
@RequiredArgsConstructor
@Transactional
public class BoardingSequenceService implements IBoardingSequenceService {

    private static final int DEFAULT_MAX_SEQUENCE = 450;
    private final BoardingSequenceRepository sequenceRepository;
    private final BoardingEventRepository eventRepository;
    private final BoardingSequenceMapper sequenceMapper;
    private final FlightRepository flightRepository;


    /** BOARDING */
    @Override
    @Transactional // 👈 CRITICAL: Ensures changes are committed to the DB
    public void board(Long flightId, Integer sequenceNumber) {
        // 1. Fetch the sequence
        BoardingSequence sequence = sequenceRepository
                .findByFlightIdAndSequenceNumber(flightId, sequenceNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Sequence #" + sequenceNumber + " not found for Flight ID: " + flightId));

        // 2. State Validation (Optional but recommended)
        if (sequence.getStatus() == BoardingStatus.BOARDED) {
            throw new IllegalStateException("Passenger is already boarded.");
        }

        // 3. Update Status
        sequence.setStatus(BoardingStatus.BOARDED);

        // 4. Log the Audit Event
        // This assumes logEvent creates a new record in a boarding_events table
        logEvent(sequence, BoardingEventType.BOARDED, "Passenger boarded at gate");

        // Note: No need for repository.save() because of @Transactional "Dirty Checking"
    }

    @Override
    @Transactional
    public void undoBoarding(Long flightId, Integer sequenceNumber) {
        // 1. Fetch the sequence
        BoardingSequence sequence = sequenceRepository
                .findByFlightIdAndSequenceNumber(flightId, sequenceNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Sequence #" + sequenceNumber + " not found for Flight ID: " + flightId));

        // 2. State Validation
        if (sequence.getStatus() != BoardingStatus.BOARDED) {
            throw new IllegalStateException("Passenger is not boarded. Current status: " + sequence.getStatus());
        }

        // 3. Revert Status back to CHECKED_IN
        sequence.setStatus(BoardingStatus.CHECKED_IN);

        // 4. Log the Audit Event
        logEvent(sequence, BoardingEventType.UNDO_BOARDED, "Boarding undone - passenger reverted to checked-in");

        // Note: No need for repository.save() because of @Transactional "Dirty Checking"
    }

    @Override
    public List<Integer> findMissing(Long flightId) {
        return sequenceRepository
                .findByFlightIdAndStatusNot(flightId, BoardingStatus.BOARDED)
                .stream()
                .map(BoardingSequence::getSequenceNumber)
                .toList();
    }

    @Override
    public void addNote(Long flightId, Integer sequenceNumber, String note) {
        // Fetch the boarding sequence
        BoardingSequence sequence = sequenceRepository
                .findByFlightIdAndSequenceNumber(flightId, sequenceNumber)
                .orElseThrow(() -> new EntityNotFoundException("Sequence not found"));

        // Update the note
        sequence.setNote(note);
        sequenceRepository.save(sequence);

        // Optionally log an event for audit trail
        logEvent(sequence, BoardingEventType.NOTE_ADDED, note);
    }

    public void prePopulateSequences(Flight flight) {

        for (int i = 1; i <= DEFAULT_MAX_SEQUENCE; i++) {
            BoardingSequence sequence = BoardingSequence.builder()
                    .flight(flight)
                    .sequenceNumber(i)
                    .status(BoardingStatus.CHECKED_IN)
                    .build();

            sequenceRepository.save(sequence);
        }
    }

    // Add this to your Service to support the "Boarding Gate" view
    @Override
    public List<BoardingSequenceDTO> getCheckedInPassengers(Long flightId) {
        return sequenceRepository.findByFlightIdAndStatus(flightId, BoardingStatus.CHECKED_IN)
                .stream()
                .map(sequenceMapper::toDTO)
                .toList();
    }

    // Update or add this method for getting boarded passengers
    @Override
    public List<BoardingSequenceDTO> getBoardedPassengers(Long flightId) {
        return sequenceRepository.findByFlightIdAndStatus(flightId, BoardingStatus.BOARDED)
                .stream()
                .map(sequenceMapper::toDTO)
                .toList();
    }

    @Override
    public Map<String, Object> getBoardingStats(Long flightId) {
        // 1. Fetch flight to get the flight number
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with ID: " + flightId));

        // 2. Count passengers at the gate
        long checkedInCount = sequenceRepository.countByFlightIdAndStatus(
                flightId, BoardingStatus.CHECKED_IN);

        // 3. Count passengers already boarded
        long boardedCount = sequenceRepository.countByFlightIdAndStatus(
                flightId, BoardingStatus.BOARDED);

        // 4. Return as Object map to allow both String and Long types
        return Map.of(
                "flightNumber", flight.getFlightNumber(),
                "checkedIn", checkedInCount,
                "boarded", boardedCount,
                "total", (checkedInCount + boardedCount)
        );
    }

    private void logEvent(BoardingSequence seq,
                          BoardingEventType type,
                          String details) {
        BoardingEvent event = new BoardingEvent();
        event.setSequence(seq);
        event.setEventType(type);
        event.setDetails(details);
        eventRepository.save(event);
    }
}
