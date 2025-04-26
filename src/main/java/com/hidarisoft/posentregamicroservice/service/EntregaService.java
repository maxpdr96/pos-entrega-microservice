package com.hidarisoft.posentregamicroservice.service;

import com.hidarisoft.posentregamicroservice.client.PedidoClient;
import com.hidarisoft.posentregamicroservice.dto.AtualizacaoStatusEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.AtualizacaoStatusPedidoDTO;
import com.hidarisoft.posentregamicroservice.dto.CriacaoEntregaDTO;
import com.hidarisoft.posentregamicroservice.dto.EntregaDTO;
import com.hidarisoft.posentregamicroservice.enums.StatusEntrega;
import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import com.hidarisoft.posentregamicroservice.enums.StatusPedido;
import com.hidarisoft.posentregamicroservice.enums.TipoEntrega;
import com.hidarisoft.posentregamicroservice.factory.EntregaStrategyFactory;
import com.hidarisoft.posentregamicroservice.mapper.EntregaMapper;
import com.hidarisoft.posentregamicroservice.model.Entrega;
import com.hidarisoft.posentregamicroservice.model.Entregador;
import com.hidarisoft.posentregamicroservice.repository.EntregaRepository;
import com.hidarisoft.posentregamicroservice.repository.EntregadorRepository;
import com.hidarisoft.posentregamicroservice.strategy.EntregaStrategy;
import com.hidarisoft.posentregamicroservice.strategy.SeletorEntregadorStrategy;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EntregaService {
    public static final String ENTREGA_NAO_ENCONTRADA_COM_ID = "Entrega não encontrada com ID: ";
    private final EntregaRepository entregaRepository;
    private final EntregadorRepository entregadorRepository;
    private final SeletorEntregadorStrategy seletorEntregadorStrategy;
    private final EntregaStrategyFactory strategyFactory;
    private final PedidoClient pedidoClient;
    private final EntregaMapper entregaMapper;

    public EntregaService(EntregaRepository entregaRepository, EntregadorRepository entregadorRepository,
                          SeletorEntregadorStrategy seletorEntregadorStrategy, EntregaStrategyFactory strategyFactory,
                          PedidoClient pedidoClient, EntregaMapper entregaMapper) {
        this.entregaRepository = entregaRepository;
        this.entregadorRepository = entregadorRepository;
        this.seletorEntregadorStrategy = seletorEntregadorStrategy;
        this.strategyFactory = strategyFactory;
        this.pedidoClient = pedidoClient;
        this.entregaMapper = entregaMapper;
    }

    @Transactional(readOnly = true)
    public List<EntregaDTO> listarTodas() {
        return entregaMapper.toDtoList(entregaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public EntregaDTO buscarPorId(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTREGA_NAO_ENCONTRADA_COM_ID + id));
        return entregaMapper.toDto(entrega);
    }

    @Transactional
    public EntregaDTO criar(CriacaoEntregaDTO criacaoDTO) {
        validarEntregaExistente(criacaoDTO.getPedidoId());

        Entrega entrega = criarNovaEntrega(criacaoDTO);

        aplicarEstrategiasDeEntrega(entrega, criacaoDTO);

        if (criacaoDTO.getTipo() != TipoEntrega.RETIRADA) {
            atribuirEntregador(entrega, criacaoDTO);
            atualizarStatusPedido(criacaoDTO.getPedidoId(), StatusPedido.EM_TRANSPORTE);
        }

        entrega = entregaRepository.save(entrega);
        return entregaMapper.toDto(entrega);
    }

    private void validarEntregaExistente(Long pedidoId) {
        if (entregaRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new IllegalStateException("Já existe uma entrega para o pedido com ID: " + pedidoId);
        }
    }

    private Entrega criarNovaEntrega(CriacaoEntregaDTO criacaoDTO) {
        Entrega entrega = new Entrega();
        entrega.setPedidoId(criacaoDTO.getPedidoId());
        entrega.setTipo(criacaoDTO.getTipo());
        entrega.setEnderecoEntrega(criacaoDTO.getEnderecoEntrega());
        entrega.setValorPedido(criacaoDTO.getValorPedido());
        entrega.setObservacoes(criacaoDTO.getObservacoes());
        entrega.setStatus(StatusEntrega.PENDENTE);
        return entrega;
    }

    private void aplicarEstrategiasDeEntrega(Entrega entrega, CriacaoEntregaDTO criacaoDTO) {
        EntregaStrategy strategy = strategyFactory.getStrategy(criacaoDTO.getTipo());
        strategy.calcularValorEntrega(entrega, criacaoDTO);
        strategy.calcularTempoEstimado(entrega, criacaoDTO);
    }

    private void atribuirEntregador(Entrega entrega, CriacaoEntregaDTO criacaoDTO) {
        Optional<Entregador> entregadorOpt = seletorEntregadorStrategy.selecionarEntregador(criacaoDTO);

        entregadorOpt.ifPresentOrElse(entregador -> {
            entrega.setEntregador(entregador);
            atualizarStatusEntregador(entregador);
        }, () -> {
            throw new IllegalStateException("Nenhum entregador disponível para o tipo de entrega: " + criacaoDTO.getTipo());
        });
    }

    private void atualizarStatusEntregador(Entregador entregador) {
        log.info("Entregador selecionado: {}", entregador);
        entregador.setStatus(StatusEntregador.EM_ENTREGA);
        entregadorRepository.save(entregador);
        log.info("Status do entregador atualizado para EM_ENTREGA");
    }

    private void atualizarStatusPedido(Long pedidoId, StatusPedido status) {
        AtualizacaoStatusPedidoDTO statusPedidoDTO = new AtualizacaoStatusPedidoDTO();
        statusPedidoDTO.setStatus(status);

        ResponseEntity<?> response = pedidoClient.atualizarStatusPedido(pedidoId, statusPedidoDTO);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Erro ao atualizar status do pedido: " + response.getStatusCode());
        }

        log.info("Status do pedido {} atualizado para {}", pedidoId, status);
    }

    @Transactional
    public EntregaDTO atualizarStatus(Long id, AtualizacaoStatusEntregaDTO statusDTO) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTREGA_NAO_ENCONTRADA_COM_ID + id));

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
        return entregaMapper.toDto(entrega);
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
        statusPedidoDTO.setStatus(StatusPedido.ENTREGUE);
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
        statusPedidoDTO.setStatus(StatusPedido.CANCELADO);
        pedidoClient.atualizarStatusPedido(entrega.getPedidoId(), statusPedidoDTO);
    }

    @Transactional
    public void excluirEntrega(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTREGA_NAO_ENCONTRADA_COM_ID + id));

        // Se houver um entregador associado, liberar o entregador
        if (entrega.getEntregador() != null) {
            Entregador entregador = entrega.getEntregador();
            entregador.setStatus(StatusEntregador.DISPONIVEL);
            entregadorRepository.save(entregador);
        }

        entregaRepository.delete(entrega);
    }

    public ResponseEntity<EntregaDTO> buscarPorPedidoId(Long pedidoId) {
        Optional<Entrega> entrega = entregaRepository.findByPedidoId(pedidoId);
        entrega.ifPresent(entregaDTO -> log.info("Entrega encontrada: {}", entregaDTO));
        return entrega.map(e -> ResponseEntity.ok(entregaMapper.toDto(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}