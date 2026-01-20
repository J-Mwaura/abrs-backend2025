package com.soekm.abrs.controller;

import com.soekm.abrs.dto.response.ApiResponse;
import com.soekm.abrs.dto.FlightDTO;
import com.soekm.abrs.service.iService.IFlightService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final IFlightService flightService;

    @PostMapping
    public ResponseEntity<ApiResponse<FlightDTO>> createFlight(@RequestBody FlightDTO flightDTO) {
        // 1. Call service (it now takes only one parameter)
        FlightDTO savedFlight = flightService.createFlightWithSequences(flightDTO);

        // 2. Return success
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Flight and " + flightDTO.getHighestSequence() + " boarding sequences initialized.",
                        savedFlight
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDTO>> getFlight(@PathVariable Long id) {
        FlightDTO flight = flightService.getFlight(id);
        return ResponseEntity.ok(ApiResponse.success("Flight fetched successfully", flight));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightDTO>>> getAllFlights() {
        List<FlightDTO> flights = flightService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Flights fetched successfully", flights));
    }

    @PostMapping("/{id}/open")
    public ResponseEntity<ApiResponse<FlightDTO>> openBoarding(@PathVariable Long id) {
        FlightDTO updatedFlight = flightService.openBoarding(id);

        ApiResponse<FlightDTO> response = new ApiResponse<>(
                true,
                "Boarding has been successfully opened for flight ID: " + id,
                updatedFlight
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<FlightDTO>> closeBoarding(@PathVariable Long id) {
        // Call the service logic you just defined
        FlightDTO updatedFlight = flightService.closeFlight(id);

        // Wrap in our standard ApiResponse
        ApiResponse<FlightDTO> response = new ApiResponse<>(
                true,
                "Boarding closed successfully. No-shows have been recorded.",
                updatedFlight
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{flightId}/status")
    public ResponseEntity<ApiResponse<String>> getFlightStatus(@PathVariable Long flightId) {
        try {
            String status = flightService.getFlightStatus(flightId);
            return ResponseEntity.ok(ApiResponse.success("Flight status retrieved", status));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Flight not found"));
        }
    }

    @DeleteMapping("/{flightId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Long flightId) {
        flightService.deleteFlightAfterManifest(flightId);

        return ResponseEntity.ok(
                ApiResponse.success("Flight deleted successfully", null)
        );
    }
}