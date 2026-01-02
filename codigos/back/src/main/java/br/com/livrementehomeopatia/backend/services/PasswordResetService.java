package br.com.livrementehomeopatia.backend.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.livrementehomeopatia.backend.model.PasswordResetToken;
import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.repository.PasswordResetTokenRepository;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelo processo de recuperação e redefinição de senha dos usuários.
 * Envia e-mails com tokens de recuperação e valida tokens recebidos para atualização segura da senha.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    /**
     * Inicia o processo de recuperação de senha.
     * Gera ou atualiza um token de redefinição de senha e envia um link por e-mail ao usuário.
     *
     * @param email o e-mail do usuário que deseja recuperar a senha
     * @throws IllegalArgumentException se o e-mail não estiver cadastrado (mensagem genérica)
     */
    public void requestPasswordRecovery(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> 
                new IllegalArgumentException("Se este e-mail estiver registrado, você receberá um link de redefinição.")
            );
    
        LocalDateTime now = LocalDateTime.now();
    
        // Tenta buscar token existente
        PasswordResetToken token = tokenRepository.findByUser(user)
            .orElse(new PasswordResetToken());
    
        // Atualiza ou cria o token
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusMinutes(5));
        token.setConfirmedAt(null); // reseta a confirmação se já houver
    
        tokenRepository.saveAndFlush(token);
    
        // Envia o e-mail com o link de recuperação
        String link = "https://farmacialivremente.azurewebsites.net/redefinir-senha?token=" + token.getToken();
mailService.sendPasswordResetEmail(user.getEmail(), "Recuperação de Senha", link);
    }

    /**
     * Realiza a redefinição de senha de um usuário com base em um token válido.
     * O token é invalidado após o uso.
     *
     * @param token o token de redefinição recebido por e-mail
     * @param newPassword a nova senha a ser definida para o usuário
     * @throws RuntimeException se o token for inválido ou estiver expirado
     */
    public void passwordReset(String token, String newPassword) {
        var prt = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (prt.isExpired()) throw new RuntimeException("Expired token");

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(prt);
    }
}
