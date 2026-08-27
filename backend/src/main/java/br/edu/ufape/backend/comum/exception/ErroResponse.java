package br.edu.ufape.backend.comum.exception;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public record ErroResponse(String message, int status, OffsetDateTime timestamp) {

	public ErroResponse(String message, int status) {
		this(message, status, OffsetDateTime.now(ZoneId.of("America/Recife")));
	}

	public String mensagem() {
		return message;
	}
}
