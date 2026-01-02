package br.com.livrementehomeopatia.backend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para redefinição de senha.
 * Contém o token de recuperação e a nova senha fornecida pelo usuário.
 */
@RequiredArgsConstructor
@Getter
@Setter
public class ResetPasswordDTO {

    /**
     * Token de recuperação de senha recebido por e-mail.
     */
    private String token;

    /**
     * Nova senha que o usuário deseja definir.
     */
    private String newPassword;
}
