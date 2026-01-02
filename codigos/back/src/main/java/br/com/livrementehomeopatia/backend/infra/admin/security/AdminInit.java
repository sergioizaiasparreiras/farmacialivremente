package br.com.livrementehomeopatia.backend.infra.admin.security;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.livrementehomeopatia.backend.infra.admin.model.Admin;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador do usuário administrador do sistema.
 * 
 * Cria automaticamente um usuário admin durante a inicialização da aplicação,
 * usando credenciais configuradas em variáveis de ambiente. O admin é persistido
 * com um telefone padrão para atender constraints do banco de dados.
 * 
 * Variáveis de ambiente necessárias:
 * - APP_ADMIN_ENABLED: Habilita/desabilita a criação (padrão: true)
 * - APP_ADMIN_EMAIL: Email do administrador (obrigatório)
 * - APP_ADMIN_PASSWORD: Senha do administrador (obrigatório)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInit {

    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    /**
     * Cria o usuário administrador durante a inicialização.
     * 
     * Executa após a aplicação estar pronta, verificando primeiro:
     * 1. Se a criação está habilitada
     * 2. Se o admin já existe no banco
     * 
     * Persiste um novo admin com:
     * - Nome fixo "Administrador"
     * - Email e senha das variáveis de ambiente
     * - Telefone padrão "00000000000"
     * 
     * @throws IllegalStateException se variáveis obrigatórias não estiverem configuradas
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAdminUser() {
        if (!Boolean.parseBoolean(env.getProperty("APP_ADMIN_ENABLED", "true"))) {
            log.info("Criação do admin desativada");
            return;
        }

        String email = env.getRequiredProperty("APP_ADMIN_EMAIL");
        String password = env.getRequiredProperty("APP_ADMIN_PASSWORD");

        boolean adminExists = em.createQuery(
            "SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email", Boolean.class)
            .setParameter("email", email)
            .getSingleResult();

        if (!adminExists) {
            Admin admin = new Admin();
            admin.setFullName("Administrador");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setPhone("00000000000");
            em.persist(admin);
            log.info("Admin criado com email: {}", email);
        }
    }
}