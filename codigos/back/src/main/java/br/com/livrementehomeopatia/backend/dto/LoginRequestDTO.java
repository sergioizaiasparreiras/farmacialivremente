package br.com.livrementehomeopatia.backend.dto;

/**
 * DTO utilizado para requisições de login.
 * <p>
 * Contém as credenciais necessárias para autenticação: e-mail e senha.
 *
 * @param email    E-mail do usuário.
 * @param password Senha do usuário.
 */
public record LoginRequestDTO(String email, String password) {
}
