package com.hidarisoft.posentregamicroservice.strategy;

import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import com.hidarisoft.posentregamicroservice.model.Entrega;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class EntregaNormalStrategy implements EntregaStrategy {

    private static final BigDecimal TAXA_FIXA = new BigDecimal("5.00");
    private static final BigDecimal PERCENTUAL_VALOR = new BigDecimal("0.05"); // 5% do valor do pedido

    @Override
    public void calcularValorEntrega(Entrega entrega, CriacaoEntregaDTO dto) {
        // Taxa fixa + percentual do valor do pedido
        BigDecimal valorPercentual = dto.getValorPedido().multiply(PERCENTUAL_VALOR);
        BigDecimal valorTotal = TAXA_FIXA.add(valorPercentual);

        // Arredonda para 2 casas decimais
        valorTotal = valorTotal.setScale(2, RoundingMode.HALF_UP);

        entrega.setValorEntrega(valorTotal);
    }

    @Override
    public void calcularTempoEstimado(Entrega entrega, CriacaoEntregaDTO dto) {
        // Tempo estimado fixo para entrega normal: 60 minutos (1 hora)
        entrega.setTempoEstimado(60);
    }

    @Override
    public boolean isEntregadorAdequado(Entregador entregador, CriacaoEntregaDTO dto) {
        // Para entrega normal, qualquer entregador disponível é adequado
        return entregador.getStatus() == StatusEntregador.DISPONIVEL;
    }
}