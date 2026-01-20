package com.soekm.abrs.service;

import com.soekm.abrs.dto.FlightDTO;
import com.soekm.abrs.dto.mapper.FlightMapper;
import com.soekm.abrs.entity.BoardingSequence;
import com.soekm.abrs.entity.Flight;
import com.soekm.abrs.entity.enums.BoardingStatus;
import com.soekm.abrs.entity.enums.FlightStatus;
import com.soekm.abrs.repository.BoardingSequenceRepository;
import com.soekm.abrs.repository.FlightRepository;
import com.soekm.abrs.service.iService.IFlightService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FlightService implements IFlightService {

    private final FlightRepository flightRepository;
    private final BoardingSequenceRepository boardingSequenceRepository;
    private final FlightMapper flightMapper;

    @Override
    @Transactional
    public FlightDTO createFlightWithSequences(FlightDTO dto) {
        Integer highestSequence = dto.getHighestSequence(); // 👈 Get it from the DTO

        if (highestSequence == null || highestSequence < 1) {
            throw new IllegalArgumentException("Highest sequence number must be at least 1.");
        }

        // 2. Map DTO to Entity first
        Flight flight = flightMapper.toEntity(dto);

        // 3. FIX: Handle the Null Departure Time
        // If the user sent a time, use it. If not, use Today's date automatically.
        LocalDate dateOfFlight;
        if (flight.getDepartureTime() != null) {
            dateOfFlight = flight.getDepartureTime().toLocalDate();
        } else {
            dateOfFlight = LocalDate.now(); // Fallback for your "Null" test case
        }

        // Set the required field on the entity
        flight.setFlightDate(dateOfFlight);

        // 4. Check for duplicates using the extracted date
        if (flightRepository.existsByFlightNumberAndFlightDate(flight.getFlightNumber(), dateOfFlight)) {
            throw new RuntimeException("Flight " + flight.getFlightNumber() + " already exists for " + dateOfFlight);
        }

        // 5. Generate 1..N sequences
        for (int i = 1; i <= highestSequence; i++) {
            BoardingSequence seq = BoardingSequence.builder()
                    .flight(flight)
                    .sequenceNumber(i)
                    .status(BoardingStatus.CHECKED_IN)
                    .build();
            flight.getSequences().add(seq);
        }

        return flightMapper.toDTO(flightRepository.save(flight));
    }

    @Override
    public FlightDTO getFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));
        return flightMapper.toDTO(flight);
    }

    @Override
    public List<FlightDTO> findAll() {
        return flightRepository.findAll()
                .stream()
                .map(flightMapper::toDTO)
                .toList();
    }

    @Override
    public FlightDTO openBoarding(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));

        if (flight.getStatus() == FlightStatus.CREATED) {
            flight.setStatus(FlightStatus.BOARDING_OPEN);
            // Any other logic, like setting an open_timestamp
            flight = flightRepository.save(flight);
        }

        return flightMapper.toDTO(flight);
    }

    @Override
    @Transactional
    public FlightDTO closeFlight(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));

        if (flight.getStatus() == FlightStatus.BOARDING_OPEN) {
            // 1. Bulk update all remaining CHECKED_IN to MISSING
            boardingSequenceRepository.updateStatusForNoShows(
                    flightId,
                    BoardingStatus.CHECKED_IN,
                    BoardingStatus.MISSING
            );

            // 2. Transition Flight state
            flight.setStatus(FlightStatus.BOARDING_CLOSED);
            flight = flightRepository.save(flight);
        }

        return flightMapper.toDTO(flight);
    }

    // private helper method
    private Flight findFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with ID: " + id));
    }

    @Override
    @Transactional
    public void deleteFlightAfterManifest(Long flightId) {
        Flight flight = findFlightById(flightId); // Uses the helper!

        // Using Enum comparison to avoid the IDE warning we discussed earlier
        if (flight.getStatus() != FlightStatus.BOARDING_CLOSED) {
            throw new IllegalStateException("Cannot delete flight. Status must be BOARDING_CLOSED.");
        }

        flightRepository.delete(flight);
        log.info("Flight {} deleted successfully.", flightId);
    }

    @Override
    public String getFlightStatus(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found with ID: " + flightId));

        return flight.getStatus().name();
    }
}