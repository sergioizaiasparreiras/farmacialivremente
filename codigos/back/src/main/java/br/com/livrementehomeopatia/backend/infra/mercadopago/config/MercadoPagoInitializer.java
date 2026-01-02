package br.com.livrementehomeopatia.backend.infra.mercadopago.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Classe de configuração responsável por inicializar o SDK do Mercado Pago
 * e por prover os Beans dos clientes da API para injeção de dependência.
 *
 * Este padrão garante a ordem correta de inicialização: primeiro o token
 * é configurado, e somente depois os clientes que dependem dele são criados.
 */
@Configuration
public class MercadoPagoInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoInitializer.class);
    
    /**
     * Bean de configuração que tem a única responsabilidade de configurar o Access Token.
     *
     * Este método é executado primeiro. Ele lê o token do ambiente,
     * o configura na classe estática do MercadoPagoConfig e retorna o próprio token.
     *
     * @param accessToken O token injetado pela anotação @Value.
     * @return O próprio Access Token configurado.
     * @throws IllegalStateException se o token não estiver definido ou a configuração falhar.
     */
    @Bean
    public String mercadoPagoAccessToken(@Value("${MERCADO_PAGO_ACCESS_TOKEN}") String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            logger.error("Access Token do Mercado Pago não foi definido. Verifique a variável de ambiente 'MERCADO_PAGO_ACCESS_TOKEN'.");
            throw new IllegalStateException("Access Token do Mercado Pago não pode ser nulo ou vazio.");
        }
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            String tokenPreview = accessToken.length() > 8 ? accessToken.substring(0, 4) + "..." + accessToken.substring(accessToken.length() - 4) : "****";
            logger.info("SDK do Mercado Pago foi configurado com sucesso. Token: {}", tokenPreview);
            return accessToken;
        } catch (RuntimeException e) { // Captura qualquer erro de runtime na inicialização
            logger.error("Falha crítica ao configurar o SDK do Mercado Pago.", e);
            throw new IllegalStateException("Falha ao inicializar o SDK do Mercado Pago.", e);
        }
    }

    /**
     * Cria o Bean do PreferenceClient, garantindo que ele só seja criado APÓS
     * o token ter sido configurado com sucesso.
     *
     * A anotação @DependsOn é a chave para a solução, pois força o Spring
     * a executar o bean "mercadoPagoAccessToken" antes de tentar criar este.
     *
     * @return uma instância de {@link PreferenceClient} gerenciada pelo Spring.
     */
    @Bean
    @DependsOn("mercadoPagoAccessToken")
    public PreferenceClient preferenceClient() {
        return new PreferenceClient();
    }

    /**
     * Cria o Bean do PaymentClient, garantindo a mesma ordem de inicialização.
     *
     * @return uma instância de {@link PaymentClient} gerenciada pelo Spring.
     */
    @Bean
    @DependsOn("mercadoPagoAccessToken")
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}