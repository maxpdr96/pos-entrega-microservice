package com.hidarisoft.posentregamicroservice.service;

import com.hidarisoft.posentregamicroservice.dto.EntregadorDTO;
import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import com.hidarisoft.posentregamicroservice.mapper.EntregadorMapper;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import com.hidarisoft.posentregamicroservice.repository.EntregadorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntregadorService {
    private final EntregadorRepository entregadorRepository;
    private final EntregadorMapper entregadorMapper;

    public EntregadorService(EntregadorRepository entregadorRepository, EntregadorMapper entregadorMapper) {
        this.entregadorRepository = entregadorRepository;
        this.entregadorMapper = entregadorMapper;
    }

    @Transactional(readOnly = true)
    public List<EntregadorDTO> listarTodos() {
        return entregadorMapper.toDtoList(entregadorRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<EntregadorDTO> listarDisponiveis() {
        return entregadorMapper.toDtoList(entregadorRepository.findByStatus(StatusEntregador.DISPONIVEL));
    }

    @Transactional(readOnly = true)
    public EntregadorDTO buscarPorId(Long id) {
        Entregador entregador = entregadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entregador não encontrado com ID: " + id));
        return entregadorMapper.toDto(entregador);
    }

    @Transactional
    public EntregadorDTO atualizar(Long id, EntregadorDTO entregadorDTO) {
        Entregador entregador = entregadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entregador não encontrado com ID: " + id));

        entregadorMapper.updateEntityFromDto(entregadorDTO, entregador);
        entregador = entregadorRepository.save(entregador);

        return entregadorMapper.toDto(entregador);
    }

    @Transactional
    public EntregadorDTO criar(EntregadorDTO entregadorDTO) {
        if (entregadorDTO.getStatus() == null) {
            entregadorDTO.setStatus(StatusEntregador.DISPONIVEL);
        }

        Entregador entregador = entregadorMapper.toEntity(entregadorDTO);
        entregador = entregadorRepository.save(entregador);

        return entregadorMapper.toDto(entregador);
    }
}
