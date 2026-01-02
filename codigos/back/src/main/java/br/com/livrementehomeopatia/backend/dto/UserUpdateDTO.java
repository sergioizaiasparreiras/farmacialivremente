package br.com.livrementehomeopatia.backend.dto;

import br.com.livrementehomeopatia.backend.validation.OnFieldPresent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO utilizado para atualizações parciais de dados de um usuário.
 */
public class UserUpdateDTO {

    @NotBlank(message = "Nome completo é obrigatório", groups = OnFieldPresent.class)
    private String fullName;

    @NotBlank(message = "E-mail é obrigatório", groups = OnFieldPresent.class)
    @Email(message = "E-mail inválido", groups = OnFieldPresent.class)
    private String email;

    @NotBlank(message = "Telefone é obrigatório", groups = OnFieldPresent.class)
    @Pattern(regexp = "\\d{10,11}", message = "Telefone inválido", groups = OnFieldPresent.class)
    private String phone;

    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres", groups = OnFieldPresent.class)
    private String password;

    // Getters e Setters
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
