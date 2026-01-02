package br.com.livrementehomeopatia.backend.infra.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Representa um usuário autenticado no sistema, contendo informações básicas
 * necessárias para operações de autorização e controle de acesso.
 * <p>
 * Esta classe é tipicamente utilizada para transportar informações do usuário logado
 * entre as camadas da aplicação após a autenticação bem-sucedida.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoggedUser {

    /**
     * Identificador único do usuário no sistema.
     */
    private Integer id;

    /**
     * Endereço de e-mail do usuário, que serve como identificador de login.
     */
    private String email;

    /**
     * Perfil de acesso do usuário (role), que define suas permissões no sistema.
     * Exemplos: ADMIN, CLIENTE, MEDICO
     */
    private String role;
}