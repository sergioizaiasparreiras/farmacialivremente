package br.com.livrementehomeopatia.backend.infra.mercadopago.controller;

import br.com.livrementehomeopatia.backend.infra.mercadopago.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Controlador que recebe e valida notificações de webhook do Mercado Pago.
 * Esta versão foi corrigida para usar o ID do recurso vindo dos parâmetros da URL
 * para a validação da assinatura, garantindo a compatibilidade.
 */
@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class MercadoPagoController {

    /**
     * Logger para registrar informações e erros relacionados a este controlador.
     */
    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoController.class);
    
    /**
     * Template para a construção da mensagem de validação da assinatura HMAC,
     * conforme especificado pela documentação do Mercado Pago.
     */
    private static final String SIGNATURE_TEMPLATE = "id:%s;request-id:%s;ts:%s;%s";

    /**
     * Serviço que contém a lógica de negócio para processar os pagamentos.
     * Injetado via construtor pelo Lombok.
     */
    private final MercadoPagoService paymentService;

    /**
     * Chave secreta para a validação dos webhooks, injetada a partir das
     * configurações da aplicação (variáveis de ambiente ou application.properties).
     */
    @Value("${MERCADO_PAGO_WEBHOOK_SECRET}")
    private String webhookSecret;

    /**
     * Recebe e processa notificações de webhook do Mercado Pago.
     *
     * @param dataId O ID do pagamento, extraído do parâmetro de URL 'data.id'.
     * @param type O tipo de evento, extraído do parâmetro de URL 'type'.
     * @param payload O corpo bruto da requisição.
     * @param signatureHeader O cabeçalho 'x-signature' para validação.
     * @param requestIdHeader O cabeçalho 'x-request-id' para validação.
     * @return Uma resposta HTTP com o status apropriado.
     */
    @PostMapping("/notificacoes")
    public ResponseEntity<Void> receiveNotifications(
            @RequestParam(name = "data.id", required = false) String dataId,
            @RequestParam(name = "type", required = false) String type,
            @RequestBody String payload,
            @RequestHeader("x-signature") String signatureHeader,
            @RequestHeader("x-request-id") String requestIdHeader) {

        logger.info("=== WEBHOOK RECEBIDO (VALIDAÇÃO POR URL) ===");
        
        if (!"payment".equals(type)) {
            logger.info("Notificação de tipo '{}' ignorada, pois não é 'payment'.", type);
            return ResponseEntity.ok().build();
        }

        if (dataId == null || dataId.isEmpty()) {
            logger.error("Notificação de pagamento recebida, mas o parâmetro 'data.id' está ausente na URL.");
            return ResponseEntity.badRequest().build();
        }

        try {
            Map<String, String> signatureParts = StreamSupport.stream(
                    java.util.Arrays.spliterator(signatureHeader.split(",")), false)
                .map(part -> part.split("=", 2))
                .collect(Collectors.toMap(p -> p[0].trim(), p -> p[1].trim()));

            String receivedTs = signatureParts.get("ts");
            String receivedHash = signatureParts.get("v1");

            if (receivedTs == null || receivedHash == null) {
                logger.warn("Cabeçalho de assinatura mal formatado: {}", signatureHeader);
                return ResponseEntity.badRequest().build();
            }

            String signedMessage = String.format(SIGNATURE_TEMPLATE, dataId, requestIdHeader, receivedTs, payload);
            String expectedHash = calculateHmacSha256(webhookSecret, signedMessage);

            logger.info("--- DEBUG DE ASSINATURA ---");
            logger.info("ID do Recurso (da URL): {}", dataId);
            logger.info("Hash Esperado (Calculado por nós): {}", expectedHash);
            logger.info("Hash Recebido (do Mercado Pago):  {}", receivedHash);
            logger.info("--- FIM DO DEBUG ---");

            if (!MessageDigest.isEqual(expectedHash.getBytes(StandardCharsets.UTF_8), receivedHash.getBytes(StandardCharsets.UTF_8))) {
                logger.warn("ASSINATURA INVÁLIDA. A requisição foi bloqueada por segurança.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            logger.info("Assinatura de webhook validada com sucesso para o pagamento ID: {}", dataId);
            
            paymentService.processWebhookNotification(dataId);
            
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Erro crítico ao processar o webhook.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Calcula a assinatura HMAC-SHA256 de uma mensagem usando uma chave secreta.
     *
     * @param secret A chave secreta (webhook secret) para assinar a mensagem.
     * @param message A mensagem a ser assinada, formatada conforme a documentação do MP.
     * @return A assinatura HMAC-SHA256 em formato hexadecimal.
     * @throws NoSuchAlgorithmException se o algoritmo HMAC-SHA256 não for encontrado.
     * @throws InvalidKeyException se a chave secreta fornecida for inválida.
     */
    private String calculateHmacSha256(String secret, String message) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
}
