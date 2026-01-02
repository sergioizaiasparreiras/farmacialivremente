package br.com.livrementehomeopatia.backend.dto;

import java.io.Serializable;
import java.time.LocalDate;

import br.com.livrementehomeopatia.backend.enums.Role;
import br.com.livrementehomeopatia.backend.model.Doctor;
import br.com.livrementehomeopatia.backend.validation.CRM;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) para a entidade {@link Doctor}.
 * Utilizado para transportar dados do médico entre as camadas do sistema.
 */
@Getter
@Setter
public class DoctorDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único do médico.
     */
    protected Integer id;

    /**
     * Nome completo do médico.
     */
    @NotNull(message = "O campo NOME é requerido")
    protected String fullName;

    /**
     * E-mail do médico.
     */
    @NotNull(message = "O campo EMAIL é requerido")
    protected String email;

    /**
     * Senha do médico.
     */
    @NotNull(message = "O campo SENHA é requerido")
    protected String password;

    /**
     * Telefone de contato do médico.
     */
    @NotNull(message = "O campo TELEFONE é requerido")
    protected String phone;

    /**
     * Registro profissional (CRM) do médico.
     */
    @NotNull(message = "O campo CRM é requerido")
    @CRM
    protected String crm;

    /**
     * Data de criação do cadastro. Definida como a data atual por padrão.
     */
    protected LocalDate createdDate;

    /**
     * Papel (role) do usuário no sistema. Sempre será {@link Role#MEDICO} neste caso.
     */
    protected Role role;

    /**
     * Construtor padrão que define o papel como MEDICO.
     */
    public DoctorDTO() {
        super();
        this.role = Role.MEDICO;
    }

    /**
     * Construtor que converte uma entidade {@link Doctor} em DTO.
     *
     * @param obj entidade Medico a ser convertida
     */
    public DoctorDTO(Doctor obj) {
        super();
        this.id = obj.getId();
        this.fullName = obj.getFullName();
        this.email = obj.getEmail();
        this.password = obj.getPassword();
        this.phone = obj.getPhone();
        this.crm = obj.getCrm();
        this.role = obj.getRole();
        this.createdDate = LocalDate.now();
    }
}
