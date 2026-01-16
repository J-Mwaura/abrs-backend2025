package com.soekm.abrs.dto.mapper;

import com.soekm.abrs.dto.FlightDTO;
import com.soekm.abrs.entity.BoardingSequence;
import com.soekm.abrs.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(componentModel = "spring", imports = { java.time.LocalDate.class})
public interface FlightMapper {

    @Mapping(source = "departureAirport", target = "origin")
    @Mapping(source = "arrivalAirport", target = "destination")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "sequences", target = "checkedInSeats", qualifiedByName = "countSequences")
    @Mapping(source = "sequences", target = "boardedSeats", qualifiedByName = "countBoardedSequences")
    @Mapping(source = "flightDate", target = "flightDate") // Map directly now
    FlightDTO toDTO(Flight flight);

    @Mapping(source = "origin", target = "departureAirport")
    @Mapping(source = "destination", target = "arrivalAirport")
    @Mapping(target = "sequences", ignore = true)
    @Mapping(target = "flightDate", expression = "java(dto.getFlightDate() != null ? dto.getFlightDate() : LocalDate.now())")
    Flight toEntity(FlightDTO dto);

    @Named("countSequences")
    default Integer countSequences(Set<BoardingSequence> sequences) {
        return sequences == null ? 0 : sequences.size();
    }

    @Named("countBoardedSequences")
    default Integer countBoardedSequences(Set<BoardingSequence> sequences) {
        if (sequences == null) return 0;
        return (int) sequences.stream()
                .filter(s -> s.getStatus() != null && "BOARDED".equals(s.getStatus().name()))
                .count();
    }
}
