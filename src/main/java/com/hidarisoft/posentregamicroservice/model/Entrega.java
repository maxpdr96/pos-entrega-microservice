package com.hidarisoft.posentregamicroservice.model;

import com.hidarisoft.posentregamicroservice.enums.StatusEntrega;
import com.hidarisoft.posentregamicroservice.enums.TipoEntrega;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pedidoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;

    @Enumerated(EnumType.STRING)
    private StatusEntrega status;

    @Enumerated(EnumType.STRING)
    private TipoEntrega tipo;

    private String enderecoEntrega;

    private BigDecimal valorEntrega;

    private BigDecimal valorPedido;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    // Tempo estimado de entrega em minutos
    private Integer tempoEstimado;

    private String observacoes;

    @PrePersist
    public void prePersist() {
        this.dataInicio = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusEntrega.PENDENTE;
        }
    }
}
