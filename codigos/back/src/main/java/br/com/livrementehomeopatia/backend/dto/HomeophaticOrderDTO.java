package br.com.livrementehomeopatia.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HomeophaticOrderDTO extends OrderDTO {
    private OrderQuoteDTO orderQuote;
} 