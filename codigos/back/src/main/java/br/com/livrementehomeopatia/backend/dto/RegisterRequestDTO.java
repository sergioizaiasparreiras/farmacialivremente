package br.com.livrementehomeopatia.backend.dto;

/**
 * DTO utilizado para requisições de registro de novo usuário.
 * <p>
 * Contém os dados necessários para o cadastro: nome, e-mail e senha.
 *
 * @param name     Nome completo do usuário.
 * @param email    E-mail do usuário.
 * @param password Senha definida pelo usuário.
 * @param phone    Número de celular do usuário
 */

public record RegisterRequestDTO(String name, String email, String password, String phone) {
}
