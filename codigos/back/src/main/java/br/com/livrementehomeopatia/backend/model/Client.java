package br.com.livrementehomeopatia.backend.model;

import br.com.livrementehomeopatia.backend.dto.ClientDTO;
import br.com.livrementehomeopatia.backend.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa um cliente do sistema.
 * Estende a classe {@link User} e define o papel (role) como {@link Role#CLIENTE}.
 */
@Entity
@Table(name = "client")
@Getter
@Setter
public class Client extends User {

    /**
     * Construtor padrão que define automaticamente o papel do usuário como CLIENTE.
     */
    public Client() {
        this.role = Role.CLIENTE;
    }

    /**
     * Construtor com todos os campos relevantes do cliente.
     *
     * @param fullName nome completo do cliente
     * @param email e-mail do cliente
     * @param password senha do cliente
     * @param phone telefone do cliente
     * @param cpf CPF do cliente (não utilizado diretamente, mas pode ser mantido para futura extensão)
     */
    public Client(String fullName, String email, String password, String phone, String cpf) {
        super(fullName, email, password, phone);
        this.role = Role.CLIENTE;
    }

    /**
     * Construtor que instancia um cliente a partir de um {@link ClientDTO}.
     *
     * @param obj DTO com os dados do cliente
     */
    public Client(ClientDTO obj) {
        this.id = obj.getId();
        this.fullName = obj.getFullName();
        this.email = obj.getEmail();
        this.password = obj.getPassword();
        this.phone = obj.getPhone(); 
        this.role = Role.CLIENTE;
        this.createdDate = obj.getCreatedDate();
    }
}
