package com.hidarisoft.posentregamicroservice.controller;


import com.entrega.gestaoentregas.dto.EntregadorDTO;
import com.entrega.gestaoentregas.service.EntregadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entregadores")
public class EntregadorController {
    @Autowired
    private EntregadorService entregadorService;

    @GetMapping
    public ResponseEntity<List<EntregadorDTO>> listarTodos() {
        List<EntregadorDTO> entregadores = entregadorService.listarTodos();
        return ResponseEntity.ok(entregadores);
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<EntregadorDTO>> listarDisponiveis() {
        List<EntregadorDTO> entregadores = entregadorService.listarDisponiveis();
        return ResponseEntity.ok(entregadores);
    }

    @PostMapping
    public ResponseEntity<EntregadorDTO> criarEntregador(@Valid @RequestBody EntregadorDTO entregadorDTO) {
        EntregadorDTO novoEntregador = entregadorService.criar(entregadorDTO);
        return new ResponseEntity<>(novoEntregador, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntregadorDTO> buscarPorId(@PathVariable Long id) {
        EntregadorDTO entregador = entregadorService.buscarPorId(id);
        return ResponseEntity.ok(entregador);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntregadorDTO> atualizarEntregador(
            @PathVariable Long id,
            @Valid @RequestBody EntregadorDTO entregadorDTO) {
        EntregadorDTO entregadorAtualizado = entregadorService.atualizar(id, entregadorDTO);
        return ResponseEntity.ok(entregadorAtualizado);
    }
}
