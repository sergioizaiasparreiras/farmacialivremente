package br.com.livrementehomeopatia.backend.dto;

/**
 * DTO utilizado para retornar informações após autenticação bem-sucedida.
 * <p>
 * Contém o nome do usuário e o token JWT gerado.
 *
 * @param token Token JWT de autenticação.
 * @param name  Nome do usuário autenticado.
 * @param email do usuário autenticado
 * @param role do usuário autenticado
 */
public record ResponseDTO(
    String token,
    String name, 
    String email,
    String role
) 
{}
