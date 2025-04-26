package com.hidarisoft.posentregamicroservice.dto;

import com.hidarisoft.posentregamicroservice.enums.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizacaoStatusPedidoDTO {
    private StatusPedido status;
}
