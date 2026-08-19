package br.edu.ufape.backend.comum.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        String mensagem,
        int status,
        LocalDateTime timestamp
) {
    public ErroResponse(String mensagem, int status) {
        this(mensagem, status, LocalDateTime.now());
    }
}