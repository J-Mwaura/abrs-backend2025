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

    /** BOARDING */
    void board(Long flightId, Integer sequenceNumber);

    /** UNDO BOARDING */
    void undoBoarding(Long flightId, Integer sequenceNumber);

    /** GAP FINDER */
    List<Integer> findMissing(Long flightId);

    void addNote(Long flightId, Integer sequenceNumber, String note);

    // Add this to your Service to support the "Boarding Gate" view
    List<BoardingSequenceDTO> getCheckedInPassengers(Long flightId);

    // Get BOARDED passengers
    List<BoardingSequenceDTO> getBoardedPassengers(Long flightId);
    Map<String, Object> getBoardingStats(Long flightId);
}
