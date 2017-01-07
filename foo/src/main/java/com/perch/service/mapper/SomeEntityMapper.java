package com.perch.service.mapper;

import com.perch.domain.*;
import com.perch.service.dto.SomeEntityDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity SomeEntity and its DTO SomeEntityDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SomeEntityMapper {

    SomeEntityDTO someEntityToSomeEntityDTO(SomeEntity someEntity);

    List<SomeEntityDTO> someEntitiesToSomeEntityDTOs(List<SomeEntity> someEntities);

    SomeEntity someEntityDTOToSomeEntity(SomeEntityDTO someEntityDTO);

    List<SomeEntity> someEntityDTOsToSomeEntities(List<SomeEntityDTO> someEntityDTOs);
}
