package com.hidarisoft.posentregamicroservice.dto;

import com.hidarisoft.posentregamicroservice.enums.TipoEntrega;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriacaoEntregaDTO {
    @NotNull(message = "O ID do pedido é obrigatório")
    private Long pedidoId;

    private Long entregadorId;

    @NotNull(message = "O tipo de entrega é obrigatório")
    private TipoEntrega tipo;

    @NotNull(message = "O endereço de entrega é obrigatório")
    private String enderecoEntrega;

    @NotNull(message = "O valor do pedido é obrigatório")
    @Positive(message = "O valor do pedido deve ser positivo")
    private BigDecimal valorPedido;

    private String observacoes;

}
