package com.soekm.abrs.service.iService;

import com.soekm.abrs.dto.BoardingSequenceDTO;
import com.soekm.abrs.entity.BoardingSequence;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

/**
 * @author James Mwaura
 * 2026
 */

public interface IBoardingSequenceService {



    // BOARDING - Only works if flight is in BOARDING_OPEN status
    void board(Long flightId, Integer sequenceNumber);

    // UNDO BOARDING
    void undoBoarding(Long flightId, Integer sequenceNumber);

    // GAP FINDER
    List<Integer> findMissing(Long flightId);

    void addNote(Long flightId, Integer sequenceNumber, String note);

    // Get CHECKED_IN passengers
    List<BoardingSequenceDTO> getCheckedInPassengers(Long flightId);

    // Get BOARDED passengers
    List<BoardingSequenceDTO> getBoardedPassengers(Long flightId);

    // Get NO_SHOW passengers (after boarding is closed)
    List<BoardingSequenceDTO> getNoShowPassengers(Long flightId);

    Map<String, Object> getBoardingStats(Long flightId);
}
