package com.soekm.abrs.controller;

import com.soekm.abrs.dto.response.ApiResponse;
import com.soekm.abrs.dto.FlightDTO;
import com.soekm.abrs.service.iService.IFlightService;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightDTO>>> getAllFlights() {
        List<FlightDTO> flights = flightService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Flights fetched successfully", flights));
    }

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
}
