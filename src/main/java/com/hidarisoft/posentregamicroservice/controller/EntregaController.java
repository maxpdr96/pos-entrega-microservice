package com.hidarisoft.posentregamicroservice.controller;


import com.hidarisoft.posentregamicroservice.dto.AtualizacaoStatusEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.EntregaDTO;
import com.hidarisoft.posentregamicroservice.service.EntregaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entregas")
public class EntregaController {
    private final EntregaService entregaService;

    public EntregaController(EntregaService entregaService) {
        this.entregaService = entregaService;
    }


    @PostMapping
    public ResponseEntity<EntregaDTO> criarEntrega(@Valid @RequestBody CriacaoEntregaDTO criacaoDTO) {
        EntregaDTO novaEntrega = entregaService.criar(criacaoDTO);
        return new ResponseEntity<>(novaEntrega, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregaDTO> buscarPorId(@PathVariable Long id) {
        EntregaDTO entrega = entregaService.buscarPorId(id);
        return ResponseEntity.ok(entrega);
    }

    @GetMapping
    public ResponseEntity<List<EntregaDTO>> listarTodas() {
        List<EntregaDTO> entregas = entregaService.listarTodas();
        return ResponseEntity.ok(entregas);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EntregaDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoStatusEntregaDTO statusDTO) {
        EntregaDTO entregaAtualizada = entregaService.atualizarStatus(id, statusDTO);
        return ResponseEntity.ok(entregaAtualizada);
    }
}
