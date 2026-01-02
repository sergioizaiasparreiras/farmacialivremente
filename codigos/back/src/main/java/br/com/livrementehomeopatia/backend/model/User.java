package br.com.livrementehomeopatia.backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import br.com.livrementehomeopatia.backend.enums.Role;
import br.com.livrementehomeopatia.backend.infra.admin.model.Admin;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Classe abstrata base para todos os usuários do sistema, como {@link Client} e
 * {@link Doctor}.
 * Define os atributos comuns e suporte à serialização polimórfica via Jackson.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class User {

    /**
     * Cuida da serialização com herança para distinguir entre tipos concretos como
     * "cliente" e "medico".
     * Utilizado durante a serialização/deserialização JSON.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Client.class, name = "Client"),
            @JsonSubTypes.Type(value = Doctor.class, name = "Doctor"),
            @JsonSubTypes.Type(value = Admin.class, name = "Admin")
    })

    /*
      Identificador único do usuário.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Integer id;

    /**
     * Nome completo do usuário.
     */
    @Column(nullable = false)
    protected String fullName;

    /**
     * E-mail do usuário, único e obrigatório.
     */
    @Column(nullable = false, unique = true)
    protected String email;

    /**
     * Senha criptografada do usuário.
     */
    @Column(nullable = false)
    protected String password;

    /**
     * Telefone de contato do usuário.
     */
    @Column(nullable = false)
    @Pattern(regexp = "\\d{10,11}")
    protected String phone;


    @Column(nullable = false)
    private boolean active = true;


    /**
     * Data de criação da conta. Formato JSON: "dd/MM/yyyy".
     */
    @JsonFormat(pattern = "dd/MM/yyyy")
    protected LocalDate createdDate = LocalDate.now();

    /**
     * Papel (perfil) do usuário no sistema. Ex: CLIENTE, MEDICO.
     */
    @Enumerated(EnumType.STRING)
    protected Role role;

    /**
     * Um usuário possui um carrinho de compras.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    protected Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    /**
     * Construtor utilizado para instanciar subclasses com os dados básicos de um
     * usuário.
     *
     * @param fullName nome completo
     * @param email    e-mail
     * @param password senha
     * @param phone    telefone
     */
    public User(String fullName, String email, String password, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}