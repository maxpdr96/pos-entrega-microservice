package com.hidarisoft.posentregamicroservice.model;

import com.hidarisoft.posentregamicroservice.enums.StatusEntregador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entregadores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entregador {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entregador_seq")
    @SequenceGenerator(name = "entregador_seq", sequenceName = "entregador_seq", allocationSize = 1)
    private Long id;

    private String nome;
    private String veiculo;

    @Enumerated(EnumType.STRING)
    private StatusEntregador status;
}