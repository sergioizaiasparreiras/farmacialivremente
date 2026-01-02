package br.com.livrementehomeopatia.backend.infra.mercadopago.dto;

import java.util.List;

/**
 * DTO para encapsular a requisição de criação de uma preferência de pagamento.
 * Este é o formato que o frontend deve enviar para o backend, associado a um pedido de revenda.
 *
 * @param items A lista de itens que compõem a compra.
 * @param resaleOrderId O ID do pedido de revenda ('ResaleOrder') gerado pelo seu sistema, para associação.
 */
public record PreferenceRequestDTO( // [CORRIGIDO] O DTO agora reflete a regra de negócio.
        List<PaymentItemDTO> items,
        String resaleOrderId 
) {}