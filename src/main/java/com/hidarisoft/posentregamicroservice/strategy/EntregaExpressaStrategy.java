package com.hidarisoft.posentregamicroservice.strategy;

import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import com.hidarisoft.posentregamicroservice.model.Entrega;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class EntregaExpressaStrategy implements EntregaStrategy {

    private static final BigDecimal TAXA_FIXA = new BigDecimal("15.00");
    private static final BigDecimal PERCENTUAL_VALOR = new BigDecimal("0.10"); // 10% do valor do pedido

    @Override
    public void calcularValorEntrega(Entrega entrega, CriacaoEntregaDTO dto) {
        // Taxa fixa mais alta + percentual maior do valor do pedido
        BigDecimal valorPercentual = dto.getValorPedido().multiply(PERCENTUAL_VALOR);
        BigDecimal valorTotal = TAXA_FIXA.add(valorPercentual);

        // Arredonda para 2 casas decimais
        valorTotal = valorTotal.setScale(2, RoundingMode.HALF_UP);

        entrega.setValorEntrega(valorTotal);
    }

    @Override
    public void calcularTempoEstimado(Entrega entrega, CriacaoEntregaDTO dto) {
        // Tempo estimado fixo para entrega expressa: 30 minutos (metade do tempo normal)
        entrega.setTempoEstimado(30);
    }

    @Override
    public boolean isEntregadorAdequado(Entregador entregador, CriacaoEntregaDTO dto) {
        // Para entrega expressa, o entregador deve estar disponível e ter veículo adequado
        // (por exemplo, moto para ser mais rápido)
        return entregador.getStatus() == StatusEntregador.DISPONIVEL &&
                entregador.getVeiculo().toLowerCase().contains("moto");
    }
}