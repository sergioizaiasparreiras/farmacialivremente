package br.com.livrementehomeopatia.backend.infra.mercadopago.dto;

import java.math.BigDecimal;

/**
 * Representa um item individual dentro de uma preferência de pagamento.
 *
 * @param title         O nome do produto ou serviço.
 * @param description   Uma breve descrição do item.
 * @param quantity      A quantidade de unidades do item.
 * @param unitPrice     O valor monetário de uma única unidade do item.
 */
public record PaymentItemDTO(
        String title,
        String description,
        Integer quantity,
        BigDecimal unitPrice
) {}