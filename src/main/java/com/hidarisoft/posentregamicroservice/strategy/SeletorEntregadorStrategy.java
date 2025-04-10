package com.hidarisoft.posentregamicroservice.strategy;

import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import com.hidarisoft.posentregamicroservice.enums.TipoEntrega;
import com.hidarisoft.posentregamicroservice.factory.EntregaStrategyFactory;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import com.hidarisoft.posentregamicroservice.repository.EntregadorRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class SeletorEntregadorStrategy {

    private final EntregadorRepository entregadorRepository;
    private final EntregaStrategyFactory strategyFactory;
    private final Random random = new Random();

    public SeletorEntregadorStrategy(EntregadorRepository entregadorRepository, EntregaStrategyFactory strategyFactory) {
        this.entregadorRepository = entregadorRepository;
        this.strategyFactory = strategyFactory;
    }


    public Optional<Entregador> selecionarEntregador(CriacaoEntregaDTO entregaDTO) {
        // Se for retirada, não precisa de entregador
        if (entregaDTO.getTipo() == TipoEntrega.RETIRADA) {
            return Optional.empty();
        }

        // Se um entregador específico foi solicitado
        if (entregaDTO.getEntregadorId() != null) {
            return entregadorRepository.findById(entregaDTO.getEntregadorId())
                    .filter(e -> e.getStatus() == StatusEntregador.DISPONIVEL);
        }

        // Obter a estratégia para o tipo de entrega
        EntregaStrategy strategy = strategyFactory.getStrategy(entregaDTO.getTipo());

        // Buscar todos os entregadores disponíveis
        List<Entregador> disponiveis = entregadorRepository.findByStatus(StatusEntregador.DISPONIVEL);

        // Filtrar entregadores adequados para o tipo de entrega
        List<Entregador> adequados = disponiveis.stream()
                .filter(entregador -> strategy.isEntregadorAdequado(entregador, entregaDTO))
                .toList();

        if (adequados.isEmpty()) {
            return Optional.empty();
        }

        // Seleciona um entregador aleatório entre os adequados
        int index = random.nextInt(adequados.size());
        return Optional.of(adequados.get(index));
    }
}
