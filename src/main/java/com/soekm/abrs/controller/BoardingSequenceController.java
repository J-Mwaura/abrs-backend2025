package com.soekm.abrs.controller;


import com.soekm.abrs.dto.BoardingNoteRequest;
import com.soekm.abrs.dto.BoardingSequenceDTO;
import com.soekm.abrs.dto.response.ApiResponse;
import com.soekm.abrs.service.iService.IBoardingSequenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boarding")
@RequiredArgsConstructor
public class BoardingSequenceController {

    private final IBoardingSequenceService sequenceService;

    @GetMapping("/{flightId}/ready-to-board") // More descriptive name
    public ResponseEntity<ApiResponse<List<BoardingSequenceDTO>>> getReady(@PathVariable Long flightId) {
        // Now actually returns people who are CHECKED_IN
        List<BoardingSequenceDTO> data = sequenceService.getCheckedInPassengers(flightId);
        return ResponseEntity.ok(ApiResponse.success("Passengers at gate fetched", data));
    }

    /** GET BOARDED PASSENGERS */
    @GetMapping("/{flightId}/boarded")
    public ResponseEntity<ApiResponse<List<BoardingSequenceDTO>>> getBoarded(
            @PathVariable Long flightId,
            @RequestParam(defaultValue = "sequenceNumber") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        // You can implement sorting logic here if needed
        List<BoardingSequenceDTO> boarded = sequenceService.getBoardedPassengers(flightId);
        return ResponseEntity.ok(ApiResponse.success("Retrieved boarded passengers", boarded));
    }

    /** BOARDING */
    @PatchMapping("/{flightId}/sequences/{sequenceNumber}/board")
    public ResponseEntity<ApiResponse<Void>> board(
            @PathVariable Long flightId,
            @PathVariable Integer sequenceNumber
    ) {
        sequenceService.board(flightId, sequenceNumber);
        return ResponseEntity.ok(
                ApiResponse.success("Passenger boarded")
        );
    }

    /** UNDO BOARDING */
    @PatchMapping("/{flightId}/sequences/{sequenceNumber}/undo-board")
    public ResponseEntity<ApiResponse<Void>> undoBoard(
            @PathVariable Long flightId,
            @PathVariable Integer sequenceNumber
    ) {
        sequenceService.undoBoarding(flightId, sequenceNumber);
        return ResponseEntity.ok(ApiResponse.success("Boarding undone - passenger reverted to checked-in"));
    }

    /** GAP FINDER */
    @GetMapping("/{flightId}/missing")
    public ResponseEntity<ApiResponse<List<Integer>>> getMissing(
            @PathVariable Long flightId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Missing passengers fetched",
                        sequenceService.findMissing(flightId)
                )
        );
    }

    @GetMapping("/{flightId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(@PathVariable Long flightId) {
        return ResponseEntity.ok(
                ApiResponse.success("Flight stats fetched", sequenceService.getBoardingStats(flightId))
        );
    }

    /** MARK NO-SHOW / ADD NOTE */
    @PostMapping("/{flightId}/sequences/{sequenceNumber}/note")
    public ResponseEntity<ApiResponse<Void>> addNote(
            @PathVariable Long flightId,
            @PathVariable Integer sequenceNumber,
            @Valid @RequestBody BoardingNoteRequest request
    ) {
        sequenceService.addNote(flightId, sequenceNumber, request.note());
        return ResponseEntity.ok(
                ApiResponse.success("Note saved successfully")
        );
    }

}
