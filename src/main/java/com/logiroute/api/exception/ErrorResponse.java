package com.logiroute.api.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String erro,
        String mensagem,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String erro, String mensagem) {
        return new ErrorResponse(status, erro, mensagem, LocalDateTime.now());
    }
}

