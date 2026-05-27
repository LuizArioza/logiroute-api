package com.logiroute.api.dto;

import com.logiroute.api.domain.Pacote;
import com.logiroute.api.domain.enums.StatusPacote;
import com.logiroute.api.domain.enums.TipoFrete;

import java.time.LocalDateTime;

public record PacoteResponseDTO(
        Long id,
        String destinatario,
        String cepDestino,
        Double pesoKg,
        TipoFrete tipoFrete,
        StatusPacote status,
        LocalDateTime dataEntradaGalpao,
        String nomeEntregador
) {
    // Factory method — converte Pacote -> DTO
    public static PacoteResponseDTO fromEntity(Pacote pacote) {
        return new PacoteResponseDTO(
                pacote.getId(),
                pacote.getDestinatario(),
                pacote.getCepDestino(),
                pacote.getPesoKg(),
                pacote.getTipoFrete(),
                pacote.getStatus(),
                pacote.getDataEntradaGalpao(),
                pacote.getEntregador() != null
                        ? pacote.getEntregador().getNome()
                        : null
        );
    }
}

