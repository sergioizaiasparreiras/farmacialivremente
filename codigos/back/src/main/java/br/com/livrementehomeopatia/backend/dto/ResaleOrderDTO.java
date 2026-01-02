package br.com.livrementehomeopatia.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResaleOrderDTO extends OrderDTO {
    private String checkoutProLink;
    private String mercadoPagoPreferenceId;
}