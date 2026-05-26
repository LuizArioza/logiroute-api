package com.logiroute.api.domain;

import com.logiroute.api.domain.enums.StatusPacote;
import com.logiroute.api.domain.enums.TipoFrete;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pacotes")
public class Pacote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private String cepDestino;

    @Column(nullable = false)
    private Double pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFrete tipoFrete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPacote status;

    @Column(nullable = false)
    private LocalDateTime dataEntradaGalpao;

    @ManyToOne
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;
}