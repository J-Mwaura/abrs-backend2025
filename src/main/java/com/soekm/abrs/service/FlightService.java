package com.soekm.abrs.service;

import com.soekm.abrs.dto.FlightDTO;
import com.soekm.abrs.dto.mapper.FlightMapper;
import com.soekm.abrs.entity.BoardingSequence;
import com.soekm.abrs.entity.Flight;
import com.soekm.abrs.entity.enums.BoardingStatus;
import com.soekm.abrs.entity.enums.FlightStatus;
import com.soekm.abrs.repository.FlightRepository;
import com.soekm.abrs.service.iService.IFlightService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightService implements IFlightService {

    private final FlightRepository flightRepository;
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
}
