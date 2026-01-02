package br.com.livrementehomeopatia.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.livrementehomeopatia.backend.model.PasswordResetToken;
import br.com.livrementehomeopatia.backend.model.User;

/**
 * Repositório JPA para a entidade {@link PasswordResetToken}.
 * Fornece métodos para recuperar tokens de redefinição de senha por token ou por usuário.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    /**
     * Busca um token de redefinição de senha pelo seu valor de token (UUID em formato String).
     *
     * @param token o valor do token
     * @return um {@link Optional} contendo o token encontrado, se existir
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Busca um token de redefinição de senha associado a um usuário específico.
     *
     * @param user o usuário ao qual o token pertence
     * @return um {@link Optional} contendo o token encontrado, se existir
     */
    Optional<PasswordResetToken> findByUser(User user);
}
