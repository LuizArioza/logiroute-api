package com.logiroute.api.dto;

import jakarta.validation.constraints.NotNull;

public record DespacharPacoteRequestDTO(

        @NotNull(message = "ID do entregador é obrigatório")
        Long entregadorId
) {}
