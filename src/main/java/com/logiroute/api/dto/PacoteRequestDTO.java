package com.logiroute.api.dto;

import com.logiroute.api.domain.enums.TipoFrete;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record PacoteRequestDTO(

        @NotBlank(message = "Destinatário é obrigatório")
        String destinatario,

        @NotBlank(message = "CEP de destino é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido. Use o formato 00000-000")
        String cepDestino,

        @NotNull(message = "Peso é obrigatório")
        @Positive(message = "Peso deve ser maior que zero")
        Double pesoKg,

        @NotNull(message = "Tipo de frete é obrigatório")
        TipoFrete tipoFrete,

        @NotNull(message = "Data de entrada no galpão é obrigatória")
        LocalDateTime dataEntradaGalpao
) {}
