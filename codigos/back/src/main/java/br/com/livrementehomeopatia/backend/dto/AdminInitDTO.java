package br.com.livrementehomeopatia.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de contas administrativas.
 * Valida dados antes de chegar na lógica de negócio.
 */
public record AdminInitDTO(
    @NotBlank(message = "Nome completo é obrigatório")
    String fullName,
    
    @Email(message = "Formato de email inválido")
    @NotBlank(message = "Email é obrigatório")
    String email,
    
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String password
) {}