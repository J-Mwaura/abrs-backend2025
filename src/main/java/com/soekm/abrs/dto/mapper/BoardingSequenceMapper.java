package com.soekm.abrs.dto.mapper;

import com.soekm.abrs.dto.BoardingSequenceDTO;
import com.soekm.abrs.entity.BoardingSequence;
import com.soekm.abrs.entity.enums.BoardingStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * @author James Mwaura
 * 2026
 */

@Mapper(componentModel = "spring")
public interface BoardingSequenceMapper {

    @Mapping(source = "flight.id", target = "flightId", defaultValue = "0L")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "status", target = "boarded", qualifiedByName = "checkIfBoarded")
    BoardingSequenceDTO toDTO(BoardingSequence sequence);

    @Named("statusToString")
    default String statusToString(BoardingStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("checkIfBoarded")
    default boolean checkIfBoarded(BoardingStatus status) {
        return status == BoardingStatus.BOARDED;
    }
}
