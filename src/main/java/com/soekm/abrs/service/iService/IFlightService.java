package com.soekm.abrs.service.iService;

import com.soekm.abrs.dto.FlightDTO;
import com.soekm.abrs.entity.Flight;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * @author James Mwaura
 * 2026
 */

public interface IFlightService {

    @Transactional
    FlightDTO createFlightWithSequences(FlightDTO dto);

    FlightDTO getFlight(Long id);

    List<FlightDTO> findAll();
}
