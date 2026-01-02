package br.com.livrementehomeopatia.backend.infra.admin.model;

import br.com.livrementehomeopatia.backend.enums.Role;
import br.com.livrementehomeopatia.backend.model.User;
import jakarta.persistence.Entity;

/**
 * Representa um administrador do sistema, com privilégios de gestão completa.
 * <p>
 * Esta classe especializa a entidade {@link User} para usuários administrativos,
 * garantindo que sempre tenham a role {@link Role#ADMIN} e um telefone padrão
 * (para satisfazer constraints de banco de dados sem necessidade de coleta no frontend).
 * </p>
 * 
 * <p>
 * <strong>Nota de implementação:</strong> O telefone é fixo como "00000000000" pois
 * administradores são funcionários internos que não precisam deste dado para autenticação.
 * </p>
 * 
 * @see User
 * @see Role
 */
@Entity
public class Admin extends User {
    
    /**
     * Constrói uma instância de Admin com configurações padrão.
     * <p>
     * Automaticamente define:
     * </p>
     * <ul>
     *   <li>Role como {@link Role#ADMIN}</li>
     *   <li>Telefone como "00000000000" (valor fictício para constraints de banco)</li>
     * </ul>
     */
    public Admin() {
        this.setRole(Role.ADMIN);
        this.setPhone("00000000000");
    }

    /**
     * Sobrescreve o setter de telefone para garantir consistência.
     * <p>
     * Administradores sempre terão telefone "00000000000", independentemente do valor informado.
     * Isso mantém a integridade com as regras de negócio que dispensam telefone para admins.
     * </p>
     * 
     * @param phone Ignorado (mantido apenas por compatibilidade com a superclasse)
     */
    @Override
    public void setPhone(String phone) {
        super.setPhone("00000000000"); // Força o valor padrão
    }
}