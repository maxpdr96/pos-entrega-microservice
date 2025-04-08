package com.hidarisoft.posentregamicroservice.mapper;


import com.hidarisoft.posentregamicroservice.dto.EntregadorDTO;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntregadorMapper {

    Entregador toEntity(EntregadorDTO dto);

    EntregadorDTO toDto(Entregador entity);

    List<EntregadorDTO> toDtoList(List<Entregador> entities);

    void updateEntityFromDto(EntregadorDTO dto, @MappingTarget Entregador entity);
}