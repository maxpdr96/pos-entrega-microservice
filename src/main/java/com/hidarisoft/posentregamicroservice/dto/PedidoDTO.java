package com.hidarisoft.posentregamicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private Long clienteId;
    private String enderecoEntrega;
    private String status;
    private LocalDateTime dataCriacao;
    private List<ItemPedidoDTO> itens;
}
