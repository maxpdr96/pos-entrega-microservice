package com.hidarisoft.posentregamicroservice.strategy;

import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.model.Entrega;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RetiradaStrategy implements EntregaStrategy {

    @Override
    public void calcularValorEntrega(Entrega entrega, CriacaoEntregaDTO dto) {
        // Não há taxa para retirada
        entrega.setValorEntrega(BigDecimal.ZERO);
    }

    @Override
    public void calcularTempoEstimado(Entrega entrega, CriacaoEntregaDTO dto) {
        // Tempo estimado para preparação: 15 minutos
        entrega.setTempoEstimado(15);
    }

    @Override
    public boolean isEntregadorAdequado(Entregador entregador, CriacaoEntregaDTO dto) {
        // Para retirada, não precisamos de entregador
        return false;
    }
}
