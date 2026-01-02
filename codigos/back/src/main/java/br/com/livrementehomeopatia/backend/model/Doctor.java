package br.com.livrementehomeopatia.backend.model;

import br.com.livrementehomeopatia.backend.dto.DoctorDTO;
import br.com.livrementehomeopatia.backend.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa um médico no sistema.
 * Estende a classe {@link User} e define o papel (role) como {@link Role#MEDICO}.
 * Possui um campo adicional obrigatório: CRM (registro profissional).
 */
@Entity
@Table(name = "doctor")
@Getter
@Setter
public class Doctor extends User {

    /**
     * CRM do médico. Deve ser único e não nulo.
     */
    @Column(nullable = false, unique = true)
    private String crm;

    /**
     * Construtor padrão que define automaticamente o papel como MEDICO.
     */
    public Doctor() {
        this.role = Role.MEDICO;
    }

    /**
     * Construtor completo com todos os campos relevantes do médico.
     *
     * @param fullName nome completo do médico
     * @param email e-mail do médico
     * @param password senha do médico
     * @param phone telefone do médico
     * @param crm número de registro profissional (CRM)
     */
    public Doctor(String fullName, String email, String password, String phone, String crm) {
        super(fullName, email, password, phone);
        this.crm = crm;
        this.role = Role.MEDICO;
    }

    /**
     * Construtor que instancia um médico a partir de um {@link DoctorDTO}.
     *
     * @param obj DTO com os dados do médico
     */
    public Doctor(DoctorDTO obj) {
        this.id = obj.getId();
        this.fullName = obj.getFullName();
        this.email = obj.getEmail();
        this.password = obj.getPassword();
        this.crm = obj.getCrm();
        this.phone = obj.getPhone();
        this.role = Role.MEDICO;
        this.createdDate = obj.getCreatedDate();
    }
}
