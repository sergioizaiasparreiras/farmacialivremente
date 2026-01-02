package br.com.livrementehomeopatia.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderQuoteDTO {
    private Integer id;
    private String fullName;
    private String phone;
    private String email;
    private String observation;
    private String medicalPrescriptionBase64;
    private AddressDTO address;
} 