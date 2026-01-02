package br.com.livrementehomeopatia.backend.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.livrementehomeopatia.backend.model.User;

/**
 * Serviço responsável pela geração, validação e manipulação de tokens JWT.
 * Implementa as operações de autenticação baseadas em JWT para a aplicação.
 */
@Service
public class TokenService {

    /**
     * Chave secreta para assinatura dos tokens JWT.
     * Injetada a partir das configurações da aplicação (application.properties/yml).
     */
    @Value("${API_SECURITY_TOKEN_SECRET}")
    private String secret;

    /**
     * Gera um novo token JWT para o usuário autenticado.
     * 
     * @param user Objeto User contendo os dados do usuário a ser autenticado
     * @return String contendo o token JWT gerado
     * @throws RuntimeException Se ocorrer um erro durante a geração do token
     */
    public String generationToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String role = "ROLE_" + user.getRole().name(); 

            return JWT.create()
                    .withIssuer("login-auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("role", role) 
                    .withExpiresAt(this.generationExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating authentication token", exception);
        }
    }

    /**
     * Valida um token JWT existente e retorna o subject (email do usuário) se válido.
     * 
     * @param token Token JWT a ser validado
     * @return String contendo o email do usuário (subject) se o token for válido,
     *         ou null se o token for inválido/expirado
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    /**
     * Gera a data/hora de expiração para os tokens JWT.
     * 
     * @return Instant representando a data/hora de expiração (2 horas no futuro)
     */
    private Instant generationExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
