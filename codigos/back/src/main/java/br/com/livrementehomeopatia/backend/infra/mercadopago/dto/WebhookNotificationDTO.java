package br.com.livrementehomeopatia.backend.infra.mercadopago.dto;

/**
 * DTO para deserializar a notificação de webhook enviada pelo Mercado Pago.
 * Esta estrutura espelha o payload JSON enviado pela plataforma de pagamentos.
 *
 * @param action O tipo de evento que ocorreu (ex: "payment.updated").
 * @param data   O objeto contendo os dados específicos do evento, como o ID do pagamento.
 */
public record WebhookNotificationDTO(
        String action,
        DataDTO data
) {
    /**
     * DTO aninhado que contém o identificador do recurso que foi alterado.
     *
     * @param id O ID do pagamento (ou outro recurso) que foi criado ou atualizado.
     */
    public record DataDTO(String id) {}
}