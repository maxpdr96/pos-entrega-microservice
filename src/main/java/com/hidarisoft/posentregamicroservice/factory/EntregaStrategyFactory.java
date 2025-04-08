package com.hidarisoft.posentregamicroservice.factory;

import com.hidarisoft.posentregamicroservice.enums.TipoEntrega;
import com.hidarisoft.posentregamicroservice.strategy.EntregaExpressaStrategy;
import com.hidarisoft.posentregamicroservice.strategy.EntregaNormalStrategy;
import com.hidarisoft.posentregamicroservice.strategy.EntregaStrategy;
import com.hidarisoft.posentregamicroservice.strategy.RetiradaStrategy;
import org.springframework.stereotype.Component;

@Component
public class EntregaStrategyFactory {

    private final EntregaNormalStrategy entregaNormalStrategy;
    private final EntregaExpressaStrategy entregaExpressaStrategy;
    private final RetiradaStrategy entregaRetiradaStrategy;

    public EntregaStrategyFactory(EntregaNormalStrategy entregaNormalStrategy, EntregaExpressaStrategy entregaExpressaStrategy, RetiradaStrategy entregaRetiradaStrategy) {
        this.entregaNormalStrategy = entregaNormalStrategy;
        this.entregaExpressaStrategy = entregaExpressaStrategy;
        this.entregaRetiradaStrategy = entregaRetiradaStrategy;
    }


    public EntregaStrategy getStrategy(TipoEntrega tipo) {
        return switch (tipo) {
            case NORMAL -> entregaNormalStrategy;
            case EXPRESSA -> entregaExpressaStrategy;
            case RETIRADA -> entregaRetiradaStrategy;
            default -> throw new IllegalArgumentException("Tipo de entrega não suportado: " + tipo);
        };
    }
}