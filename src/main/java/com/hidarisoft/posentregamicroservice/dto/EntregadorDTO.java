package com.hidarisoft.posentregamicroservice.dto;

import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntregadorDTO {
    private Long id;

    @NotBlank(message = "O nome do entregador é obrigatório")
    private String nome;

    @NotBlank(message = "O veículo é obrigatório")
    private String veiculo;

    private StatusEntregador status;
}
