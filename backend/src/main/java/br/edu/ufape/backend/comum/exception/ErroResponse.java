package br.edu.ufape.backend.comum.exception;

import java.time.OffsetDateTime;

public record ErroResponse(
        String message,
        int status,
        OffsetDateTime timestamp
) {
    public ErroResponse(String message, int status) {
        this(message, status, OffsetDateTime.now());
    }

    public String mensagem() {
        return message;
    }
}