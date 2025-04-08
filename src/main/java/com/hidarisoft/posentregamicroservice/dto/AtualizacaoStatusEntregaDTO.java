package com.hidarisoft.posentregamicroservice.dto;

import com.hidarisoft.posentregamicroservice.enums.StatusEntrega;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizacaoStatusEntregaDTO {
    @NotNull(message = "O status é obrigatório")
    private StatusEntrega status;
}