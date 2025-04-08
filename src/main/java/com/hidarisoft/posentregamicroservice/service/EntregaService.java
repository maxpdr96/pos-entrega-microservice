package com.hidarisoft.posentregamicroservice.service;

import com.hidarisoft.posentregamicroservice.client.PedidoClient;
import com.hidarisoft.posentregamicroservice.dto.AtualizacaoStatusEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.AtualizacaoStatusPedidoDTO;
import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.EntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.PedidoDTO;
import com.hidarisoft.posentregamicroservice.enums.StatusEntrega;
import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import com.hidarisoft.posentregamicroservice.enums.TipoEntrega;
import com.hidarisoft.posentregamicroservice.factory.EntregaStrategyFactory;
import com.hidarisoft.posentregamicroservice.model.Entrega;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import com.hidarisoft.posentregamicroservice.repository.EntregaRepository;
import com.hidarisoft.posentregamicroservice.repository.EntregadorRepository;
import com.hidarisoft.posentregamicroservice.strategy.EntregaStrategy;
import com.hidarisoft.posentregamicroservice.strategy.SeletorEntregadorStrategy;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntregaService {
    private EntregaRepository entregaRepository;
    private EntregadorRepository entregadorRepository;
    private SeletorEntregadorStrategy seletorEntregadorStrategy;
    private EntregaStrategyFactory strategyFactory;
    private PedidoClient pedidoClient;

    public EntregaService(EntregaRepository entregaRepository, EntregadorRepository entregadorRepository, SeletorEntregadorStrategy seletorEntregadorStrategy, EntregaStrategyFactory strategyFactory, PedidoClient pedidoClient) {
        this.entregaRepository = entregaRepository;
        this.entregadorRepository = entregadorRepository;
        this.seletorEntregadorStrategy = seletorEntregadorStrategy;
        this.strategyFactory = strategyFactory;
        this.pedidoClient = pedidoClient;
    }

    @Transactional(readOnly = true)
    public List<EntregaDTO> listarTodas() {
        return entregaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EntregaDTO buscarPorId(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrega não encontrada com ID: " + id));
        return converterParaDTO(entrega);
    }

    @Transactional
    public EntregaDTO criar(CriacaoEntregaDTO criacaoDTO) {
        // Verificar se o pedido existe consultando o serviço de pedidos
        PedidoDTO pedidoDTO = pedidoClient.buscarPedidoPorId(criacaoDTO.getPedidoId()).getBody();
        if (pedidoDTO == null) {
            throw new EntityNotFoundException("Pedido não encontrado com ID: " + criacaoDTO.getPedidoId());
        }

        // Verificar se já existe uma entrega para este pedido
        if (entregaRepository.findByPedidoId(criacaoDTO.getPedidoId()).isPresent()) {
            throw new IllegalStateException("Já existe uma entrega para o pedido com ID: " + criacaoDTO.getPedidoId());
        }

        // Criar a entrega
        Entrega entrega = new Entrega();
        entrega.setPedidoId(criacaoDTO.getPedidoId());
        entrega.setTipo(criacaoDTO.getTipo());
        entrega.setEnderecoEntrega(criacaoDTO.getEnderecoEntrega());
        entrega.setValorPedido(criacaoDTO.getValorPedido());
        entrega.setObservacoes(criacaoDTO.getObservacoes());
        entrega.setStatus(StatusEntrega.PENDENTE);

        // Obter a estratégia para o tipo de entrega
        EntregaStrategy strategy = strategyFactory.getStrategy(criacaoDTO.getTipo());

        // Calcular valor e tempo estimado usando a estratégia
        strategy.calcularValorEntrega(entrega, criacaoDTO);
        strategy.calcularTempoEstimado(entrega, criacaoDTO);

        // Selecionar um entregador (se aplicável)
        if (criacaoDTO.getTipo() != TipoEntrega.RETIRADA) {
            Optional<Entregador> entregadorOpt = seletorEntregadorStrategy.selecionarEntregador(criacaoDTO);

            if (entregadorOpt.isPresent()) {
                Entregador entregador = entregadorOpt.get();
                entrega.setEntregador(entregador);

                // Atualizar o status do entregador
                entregador.setStatus(StatusEntregador.EM_ENTREGA);
                entregadorRepository.save(entregador);
            } else if (criacaoDTO.getTipo() != TipoEntrega.RETIRADA) {
                // Se não for retirada e não encontrar entregador adequado, notificar
                throw new IllegalStateException("Nenhum entregador disponível para o tipo de entrega: " + criacaoDTO.getTipo());
            }
        }

        // Se for retirada, não precisamos atualizar o status do pedido para "EM_TRANSPORTE"
        if (criacaoDTO.getTipo() != TipoEntrega.RETIRADA) {
            // Atualizar o status do pedido para "EM_TRANSPORTE"
            AtualizacaoStatusPedidoDTO statusPedidoDTO = new AtualizacaoStatusPedidoDTO();
            statusPedidoDTO.setStatus("EM_TRANSPORTE");
            pedidoClient.atualizarStatusPedido(criacaoDTO.getPedidoId(), statusPedidoDTO);
        }

        // Salvar a entrega
        entrega = entregaRepository.save(entrega);
        return converterParaDTO(entrega);
    }

    @Transactional
    public EntregaDTO atualizarStatus(Long id, AtualizacaoStatusEntregaDTO statusDTO) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrega não encontrada com ID: " + id));

        // Atualizar o status da entrega
        entrega.setStatus(statusDTO.getStatus());

        // Tratamento específico para cada tipo de status
        switch (statusDTO.getStatus()) {
            case ENTREGUE:
                finalizarEntrega(entrega);
                break;
            case CANCELADA:
                cancelarEntrega(entrega);
                break;
            case EM_ROTA:
                // Caso específico para entrega em rota
                if (entrega.getEntregador() == null && entrega.getTipo() != TipoEntrega.RETIRADA) {
                    throw new IllegalStateException("Não é possível marcar como EM_ROTA uma entrega sem entregador");
                }
                break;
            default:
                // Para outros status, não há lógica específica
                break;
        }

        entrega = entregaRepository.save(entrega);
        return converterParaDTO(entrega);
    }

    private void finalizarEntrega(Entrega entrega) {
        entrega.setDataFim(LocalDateTime.now());

        // Atualizar o status do entregador para disponível (se houver)
        if (entrega.getEntregador() != null) {
            Entregador entregador = entrega.getEntregador();
            entregador.setStatus(StatusEntregador.DISPONIVEL);
            entregadorRepository.save(entregador);
        }

        // Atualizar o status do pedido para "ENTREGUE"
        AtualizacaoStatusPedidoDTO statusPedidoDTO = new AtualizacaoStatusPedidoDTO();
        statusPedidoDTO.setStatus("ENTREGUE");
        pedidoClient.atualizarStatusPedido(entrega.getPedidoId(), statusPedidoDTO);
    }

    private void cancelarEntrega(Entrega entrega) {
        entrega.setDataFim(LocalDateTime.now());

        // Atualizar o status do entregador para disponível (se houver)
        if (entrega.getEntregador() != null) {
            Entregador entregador = entrega.getEntregador();
            entregador.setStatus(StatusEntregador.DISPONIVEL);
            entregadorRepository.save(entregador);
        }

        // Atualizar o status do pedido para "CANCELADO"
        AtualizacaoStatusPedidoDTO statusPedidoDTO = new AtualizacaoStatusPedidoDTO();
        statusPedidoDTO.setStatus("CANCELADO");
        pedidoClient.atualizarStatusPedido(entrega.getPedidoId(), statusPedidoDTO);
    }

    private EntregaDTO converterParaDTO(Entrega entrega) {
        EntregaDTO dto = new EntregaDTO();
        dto.setId(entrega.getId());
        dto.setPedidoId(entrega.getPedidoId());

        if (entrega.getEntregador() != null) {
            dto.setEntregadorId(entrega.getEntregador().getId());
            dto.setNomeEntregador(entrega.getEntregador().getNome());
        }

        dto.setStatus(entrega.getStatus());
        dto.setTipo(entrega.getTipo());
        dto.setEnderecoEntrega(entrega.getEnderecoEntrega());
        dto.setValorEntrega(entrega.getValorEntrega());
        dto.setValorPedido(entrega.getValorPedido());
        dto.setDataInicio(entrega.getDataInicio());
        dto.setDataFim(entrega.getDataFim());
        dto.setTempoEstimado(entrega.getTempoEstimado());
        dto.setObservacoes(entrega.getObservacoes());

        return dto;
    }
}