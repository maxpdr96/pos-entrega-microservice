package com.hidarisoft.posentregamicroservice.controller;


import com.hidarisoft.posentregamicroservice.dto.AtualizacaoStatusEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.EntregaDTO;
import com.hidarisoft.posentregamicroservice.service.EntregaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entregas")
public class EntregaController {
    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EntregaDTO> criarEntrega(@Valid @RequestBody CriacaoEntregaDTO criacaoDTO) {
        EntregaDTO novaEntrega = entregaService.criar(criacaoDTO);
        return new ResponseEntity<>(novaEntrega, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTREGADOR', 'ADMIN', 'CLIENTE')")
    public ResponseEntity<EntregaDTO> buscarPorId(@PathVariable Long id) {
        EntregaDTO entrega = entregaService.buscarPorId(id);
        return ResponseEntity.ok(entrega);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EntregaDTO>> listarTodas() {
        List<EntregaDTO> entregas = entregaService.listarTodas();
        return ResponseEntity.ok(entregas);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ENTREGADOR', 'ADMIN')")
    public ResponseEntity<EntregaDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoStatusEntregaDTO statusDTO) {
        EntregaDTO entregaAtualizada = entregaService.atualizarStatus(id, statusDTO);
        return ResponseEntity.ok(entregaAtualizada);
    }
}
