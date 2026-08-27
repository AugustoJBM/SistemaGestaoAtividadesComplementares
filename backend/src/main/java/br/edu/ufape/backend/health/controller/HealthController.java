package br.edu.ufape.backend.health.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

	private static final String STATUS = "status";
	private static final String DATABASE = "database";
	private static final String UP = "UP";
	private static final String DOWN = "DOWN";

	private final DataSource dataSource;

	public HealthController(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@GetMapping
	public ResponseEntity<Map<String, String>> health() {
		return ResponseEntity.ok(Map.of(STATUS, UP));
	}

	@GetMapping("/database")
	public ResponseEntity<Map<String, String>> databaseHealth() {
		try (Connection connection = dataSource.getConnection()) {
			if (connection.isValid(2)) {
				return ResponseEntity.ok(Map.of(STATUS, UP, DATABASE, UP));
			}
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(STATUS, DOWN, DATABASE, DOWN));
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(STATUS, DOWN, DATABASE, DOWN));
		}
	}
}
