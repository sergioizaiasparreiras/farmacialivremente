package br.com.livrementehomeopatia.backend.dto;

import java.io.Serializable;
import java.time.LocalDate;

import br.com.livrementehomeopatia.backend.enums.Role;
import br.com.livrementehomeopatia.backend.model.Client;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) para a entidade {@link Client}.
 * Utilizado para transferir dados entre camadas do sistema de forma segura.
 */
@Getter
@Setter
public class ClientDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único do cliente.
     */
    protected Integer id;

    /**
     * Nome completo do cliente.
     */
    @NotNull(message = "O campo NOME é requerido")
    protected String fullName;

    /**
     * E-mail do cliente.
     */
    @NotNull(message = "O campo EMAIL é requerido")
    protected String email;

    /**
     * Senha do cliente.
     */
    @NotNull(message = "O campo SENHA é requerido")
    protected String password;

    /**
     * Telefone de contato do cliente.
     */
    @NotNull(message = "O campo TELEFONE é requerido")
    protected String phone;

    /**
     * Data de criação da conta. Definida como a data atual por padrão.
     */
    protected LocalDate createdDate = LocalDate.now();

    /**
     * Papel do usuário no sistema. Neste caso, será sempre {@link Role#CLIENTE}.
     */
    protected Role role;

    /**
     * Construtor padrão. Inicializa o papel como CLIENTE.
     */
    public ClientDTO() {
        super();
        this.role = Role.CLIENTE;
    }

    /**
     * Construtor que converte uma entidade {@link Client} em DTO.
     *
     * @param obj objeto da entidade Cliente
     */
    public ClientDTO(Client obj) {
        super();
        this.id = obj.getId();
        this.fullName = obj.getFullName();
        this.email = obj.getEmail();
        this.password = obj.getPassword();
        this.phone = obj.getPhone();
        this.role = obj.getRole();
        this.createdDate = LocalDate.now();
    }
}
