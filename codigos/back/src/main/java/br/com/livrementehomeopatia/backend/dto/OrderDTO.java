package br.com.livrementehomeopatia.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.com.livrementehomeopatia.backend.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class OrderDTO {
    private Integer id;
    private String fullName;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> items;
    private Double totalValue;
    private String status;
    private String neighborhoodName; 
    private Double deliveryTax; 
    private Double totalWithDelivery; 
    private ProductType orderType;
}