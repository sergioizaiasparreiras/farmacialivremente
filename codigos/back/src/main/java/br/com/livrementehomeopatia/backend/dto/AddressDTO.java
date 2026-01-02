package br.com.livrementehomeopatia.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressDTO {
    private String cep;
    private String rua;
    private Integer numero;
    private String complemento;
} 