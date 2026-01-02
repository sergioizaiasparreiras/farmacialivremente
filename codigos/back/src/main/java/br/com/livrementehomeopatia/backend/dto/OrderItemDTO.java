package br.com.livrementehomeopatia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private String productName;
    private int quantity;
    private Double price;
    private String productImageBase64;

    
}