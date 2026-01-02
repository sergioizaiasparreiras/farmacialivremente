package br.com.livrementehomeopatia.backend.infra.mercadopago.service;

import br.com.livrementehomeopatia.backend.enums.OrderStatus;
import br.com.livrementehomeopatia.backend.infra.mercadopago.dto.PreferenceRequestDTO;
import br.com.livrementehomeopatia.backend.infra.mercadopago.dto.PreferenceResponseDTO;
import br.com.livrementehomeopatia.backend.model.ResaleOrder;
import br.com.livrementehomeopatia.backend.repository.ResaleOrderRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Serviço responsável por gerenciar a integração com a API do Mercado Pago.
 * Realiza operações relacionadas a pagamentos, incluindo criação de preferências
 * e processamento de notificações de webhook.
 */
@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoService.class);

    private final PreferenceClient preferenceClient;
    private final PaymentClient paymentClient;
    private final ResaleOrderRepository resaleOrderRepository;

    @Value("${APP_FRONTEND_URL}")
    private String frontendUrl;

    @Value("${APP_BACKEND_URL}")
    private String backendUrl;
    
    /**
     * Cria uma preferência de pagamento no Mercado Pago para um pedido específico.
     * @param requestDTO DTO contendo os itens do pedido e o ID do pedido de revenda.
     * @return DTO com o ID da preferência criada e a URL para pagamento.
     * @throws MPException Quando ocorre um erro geral no SDK do Mercado Pago.
     * @throws MPApiException Quando a API do Mercado Pago retorna um erro de negócio.
     */
    @Transactional
    public PreferenceResponseDTO createPaymentPreference(PreferenceRequestDTO requestDTO) throws MPException, MPApiException {
        try {
            logger.info("Iniciando criação de preferência para o ResaleOrder ID: {}", requestDTO.resaleOrderId());

            var items = requestDTO.items().stream()
                    .map(itemDTO -> PreferenceItemRequest.builder()
                            .title(itemDTO.title())
                            .description(itemDTO.description())
                            .quantity(itemDTO.quantity())
                            .currencyId("BRL")
                            .unitPrice(itemDTO.unitPrice())
                            .build())
                    .collect(Collectors.toList());

            // Remove barra final da URL para evitar barras duplas na concatenação
            String baseUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
            String returnUrl = baseUrl + "/pagamento-pix?orderId=" + requestDTO.resaleOrderId();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(returnUrl)
                    .failure(returnUrl)
                    .pending(returnUrl)
                    .build();
            
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .notificationUrl(backendUrl + "/api/pagamentos/notificacoes")
                    .autoReturn("approved")
                    .externalReference(requestDTO.resaleOrderId())
                    .build();
            
            Preference preference = preferenceClient.create(preferenceRequest);

            updateResaleOrderWithPaymentData(requestDTO.resaleOrderId(), preference);

            logger.info("Preferência de pagamento criada com sucesso. ID: {}. Associada ao ResaleOrder ID: {}", preference.getId(), requestDTO.resaleOrderId());
            return new PreferenceResponseDTO(preference.getId(), preference.getInitPoint());
        
        } catch (MPApiException e) {
            logger.error("MPApiException ao criar preferência. Status: {}, Resposta: {}", e.getApiResponse().getStatusCode(), e.getApiResponse().getContent(), e);
            throw e;
        } catch (MPException e) {
            logger.error("MPException ao criar preferência: {}", e.getMessage(), e);
            throw e;
        } catch (RuntimeException e) {
            logger.error("RuntimeException inesperada ao criar preferência.", e);
            throw new RuntimeException("Erro inesperado ao criar pagamento: " + e.getMessage(), e);
        }
    }
    
    /**
     * Processa uma notificação de webhook recebida do Mercado Pago,
     * atualizando o status do pedido conforme o status do pagamento.
     * @param paymentId ID do pagamento recebido na notificação.
     */
    @Transactional
    public void processWebhookNotification(String paymentId) {
        try {
            logger.info("Processando notificação para o pagamento de ID: {}", paymentId);

            Payment payment = paymentClient.get(Long.parseLong(paymentId));

            String resaleOrderIdStr = payment.getExternalReference();
            if (resaleOrderIdStr == null) {
                logger.warn("Notificação recebida para o pagamento ID {} sem uma external_reference (resaleOrderId). Ignorando.", paymentId);
                return;
            }

            ResaleOrder order = resaleOrderRepository.findById(Integer.parseInt(resaleOrderIdStr))
                    .orElseThrow(() -> new RuntimeException("CRÍTICO: Pedido de revenda " + resaleOrderIdStr + " não encontrado para o pagamento " + paymentId));
            
            if (order.getStatus() == OrderStatus.AGUARDANDO_PAGAMENTO && "approved".equals(payment.getStatus())) {
                order.setStatus(OrderStatus.PAGO);
                order.setMercadoPagoPaymentId(payment.getId()); 
                resaleOrderRepository.save(order);
                logger.info("ResaleOrder ID {} foi PAGO via webhook. Status atualizado.", order.getId());
            } else {
                logger.warn("Webhook para o ResaleOrder ID {} ignorado. Status atual do pedido: {}. Status do pagamento MP: {}", 
                        order.getId(), order.getStatus(), payment.getStatus());
            }

        } catch (MPApiException e) {
            logger.error("MPApiException ao buscar detalhes do pagamento ID {}. Resposta: {}", paymentId, e.getApiResponse().getContent(), e);
        } catch (MPException e) {
            logger.error("MPException ao processar webhook para pagamento ID {}: {}", paymentId, e.getMessage(), e);
        } catch (RuntimeException e) {
            logger.error("Erro inesperado (RuntimeException) ao processar a notificação para o pagamento ID {}.", paymentId, e);
        }
    }

    /**
     * Atualiza um pedido de revenda com os dados retornados pelo Mercado Pago.
     * @param resaleOrderId ID do pedido de revenda a ser atualizado.
     * @param preference Objeto de preferência retornado pelo Mercado Pago.
     * @throws RuntimeException Quando o pedido não é encontrado no banco de dados.
     */
    private void updateResaleOrderWithPaymentData(String resaleOrderId, Preference preference) {
        ResaleOrder order = resaleOrderRepository.findById(Integer.parseInt(resaleOrderId))
                .orElseThrow(() -> new RuntimeException("Pedido de revenda " + resaleOrderId + " não encontrado ao tentar salvar a preferência."));
        
        order.setMercadoPagoPreferenceId(preference.getId());
        order.setCheckoutProLink(preference.getInitPoint());

        resaleOrderRepository.save(order);
    }
}