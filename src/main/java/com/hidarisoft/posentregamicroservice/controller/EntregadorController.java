package com.hidarisoft.posentregamicroservice.controller;


import com.hidarisoft.posentregamicroservice.dto.EntregadorDTO;
import com.hidarisoft.posentregamicroservice.service.EntregadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entregadores")
public class EntregadorController {
    private final EntregadorService entregadorService;

    public EntregadorController(EntregadorService entregadorService) {
        this.entregadorService = entregadorService;
    }


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
