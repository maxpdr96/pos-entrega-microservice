package com.hidarisoft.posentregamicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private Long id;
    private String produto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}