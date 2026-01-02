package br.com.livrementehomeopatia.backend.dto;

/**
 * DTO de resposta após o cadastro de cliente ou médico
 */

public record RegisterResponseDTO(
    String name,
    String token
) {}
