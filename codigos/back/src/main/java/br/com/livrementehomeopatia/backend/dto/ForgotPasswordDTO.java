package br.com.livrementehomeopatia.backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para requisições de recuperação de senha.
 * Contém apenas o e-mail do usuário que solicitou a redefinição.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ForgotPasswordDTO {

    /**
     * E-mail do usuário que deseja recuperar a senha.
     */
    private String email;
}
