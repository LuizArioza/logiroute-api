package com.logiroute.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entregadores")
public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String veiculo; // Ex: MOTO, CARRO, VAN

    @Column(nullable = false)
    private Double capacidadeMaximaKg;

    @Column(nullable = false)
    private String cepAtendimento; // CEP base de atuação do entregador
}