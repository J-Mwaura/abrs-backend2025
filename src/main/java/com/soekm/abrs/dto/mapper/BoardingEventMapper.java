package com.soekm.abrs.dto.mapper;

import com.soekm.abrs.dto.BoardingEventDTO;
import com.soekm.abrs.entity.BoardingEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BoardingEventMapper {

    @Mapping(source = "sequence.flight.id", target = "flightId")
    @Mapping(source = "sequence.id", target = "sequenceId")
    @Mapping(source = "sequence.sequenceNumber", target = "sequenceNumber")
    @Mapping(source = "staffUser.id", target = "staffUserId")
    BoardingEventDTO toDto(BoardingEvent event);
}
