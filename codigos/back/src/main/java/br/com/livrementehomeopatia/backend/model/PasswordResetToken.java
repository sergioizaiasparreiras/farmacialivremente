package br.com.livrementehomeopatia.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa um token de redefinição de senha.
 * <p>
 * Esta classe armazena informações sobre tokens temporários utilizados
 * no processo de recuperação de senha dos usuários.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    /**
     * Identificador único do token.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Valor do token gerado.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Usuário associado a este token.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Data e hora de criação do token.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora de expiração do token.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Data e hora de confirmação do token (quando utilizado).
     */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    /**
     * Construtor para criação de um novo token de redefinição.
     *
     * @param token Valor do token
     * @param user Usuário associado
     * @param createdAt Data de criação
     * @param expiresAt Data de expiração
     */
    public PasswordResetToken(String token, User user, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    /**
     * Verifica se o token está expirado.
     *
     * @return true se o token estiver expirado, false caso contrário
     */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * Verifica se o token já foi utilizado.
     *
     * @return true se o token foi confirmado, false caso contrário
     */
    public boolean isUsed() {
        return confirmedAt != null;
    }

    /**
     * Método executado automaticamente antes de persistir a entidade.
     * Define a data de criação como o momento atual.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}