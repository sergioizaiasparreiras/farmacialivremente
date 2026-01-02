package br.com.livrementehomeopatia.backend.infra.mercadopago.dto;

/**
 * DTO que encapsula a resposta do backend para o frontend após criar uma preferência.
 *
 * @param preferenceId O identificador único da preferência gerado pelo Mercado Pago.
 * @param initPoint    A URL de redirecionamento para o Checkout Pro do Mercado Pago,
 * para onde o usuário deve ser enviado para efetuar o pagamento.
 */
public record PreferenceResponseDTO(
        String preferenceId,
        String initPoint
) {}