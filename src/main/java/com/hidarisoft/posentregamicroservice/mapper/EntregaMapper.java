package com.hidarisoft.posentregamicroservice.mapper;


import com.hidarisoft.posentregamicroservice.dto.EntregaDTO;
import com.hidarisoft.posentregamicroservice.model.Entrega;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntregaMapper {

    @Mapping(target = "entregadorId", source = "entregador.id")
    @Mapping(target = "nomeEntregador", source = "entregador.nome")
    EntregaDTO toDto(Entrega entity);

    @Mapping(target = "entregador", ignore = true)
    Entrega toEntity(EntregaDTO dto);

    List<EntregaDTO> toDtoList(List<Entrega> entities);

    @Mapping(target = "entregador", ignore = true)
    void updateEntityFromDto(EntregaDTO dto, @MappingTarget Entrega entity);
}