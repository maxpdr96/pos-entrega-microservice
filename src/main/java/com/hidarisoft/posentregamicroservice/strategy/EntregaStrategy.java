package com.hidarisoft.posentregamicroservice.strategy;

import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.model.Entrega;
import com.hidarisoft.posentregamicroservice.model.Entregador;

public interface EntregaStrategy {
    /**
     * Calcula o valor da entrega com base no tipo
     */
    void calcularValorEntrega(Entrega entrega, CriacaoEntregaDTO dto);

    /**
     * Calcula o tempo estimado de entrega em minutos
     */
    void calcularTempoEstimado(Entrega entrega, CriacaoEntregaDTO dto);

    /**
     * Verifica se um entregador é adequado para o tipo de entrega
     */
    boolean isEntregadorAdequado(Entregador entregador, CriacaoEntregaDTO dto);
}
