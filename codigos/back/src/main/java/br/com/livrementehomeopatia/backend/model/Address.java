package br.com.livrementehomeopatia.backend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {
    private String cep;
    private String rua;
    private Integer numero;
    private String complemento;
}