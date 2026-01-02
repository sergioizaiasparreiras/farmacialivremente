package br.com.livrementehomeopatia.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import br.com.livrementehomeopatia.backend.enums.OrderStatus;

/**
 * Entidade que representa um pedido de revenda com integração ao Checkout Pro do Mercado Pago.
 * <p>
 * Esta classe estende Order e adiciona funcionalidades específicas para pagamentos via
 * Checkout Pro, incluindo armazenamento do link de pagamento e ID da preferência.
 * </p>
 *
 * @author Sistema Farmácia Livremente
 * @version 2.0 - Refatorada para Checkout Pro
 * @see Order
 * @see OrderStatus
 */
@Entity
@Table(name = "resale_orders")
public class ResaleOrder extends Order {

    /**
     * Link do Checkout Pro gerado pelo Mercado Pago.
     * Este é o link para onde o cliente será redirecionado para efetuar o pagamento.
     */
    @Column(name = "checkout_pro_link", columnDefinition = "TEXT")
    private String checkoutProLink;

    /**
     * ID da preferência criada no Mercado Pago.
     * Usado para rastrear e identificar a transação no sistema do Mercado Pago.
     */
    @Column(name = "mercado_pago_preference_id")
    private String mercadoPagoPreferenceId;

    /**
     * ID do pagamento no Mercado Pago após a conclusão da transação.
     * Preenchido quando o webhook de confirmação de pagamento é recebido.
     */
    @Column(name = "mercado_pago_payment_id")
    private Long mercadoPagoPaymentId;

    /**
     * Construtor padrão necessário para JPA.
     */
    public ResaleOrder() {
        super();
    }

    /**
     * Construtor completo para criação de pedidos de revenda.
     *
     * @param user Usuário que fez o pedido
     * @param neighborhood Bairro para entrega
     * @param deliveryTax Taxa de entrega
     * @param createdAt Data e hora de criação
     * @param status Status inicial do pedido
     */
    public ResaleOrder(User user, Neighborhood neighborhood, Double deliveryTax,
                       LocalDateTime createdAt, OrderStatus status) {
        super(user, neighborhood, deliveryTax, createdAt, status);
        this.checkoutProLink = null;
        this.mercadoPagoPreferenceId = null;
        this.mercadoPagoPaymentId = null;
    }

    /**
     * Obtém o link do Checkout Pro.
     *
     * @return Link do Checkout Pro ou null se ainda não foi gerado
     */
    public String getCheckoutProLink() {
        return checkoutProLink;
    }

    /**
     * Define o link do Checkout Pro.
     *
     * @param checkoutProLink Link gerado pelo Mercado Pago
     */
    public void setCheckoutProLink(String checkoutProLink) {
        this.checkoutProLink = checkoutProLink;
    }

    /**
     * Obtém o ID da preferência no Mercado Pago.
     *
     * @return ID da preferência ou null se ainda não foi criada
     */
    public String getMercadoPagoPreferenceId() {
        return mercadoPagoPreferenceId;
    }

    /**
     * Define o ID da preferência no Mercado Pago.
     *
     * @param mercadoPagoPreferenceId ID da preferência criada
     */
    public void setMercadoPagoPreferenceId(String mercadoPagoPreferenceId) {
        this.mercadoPagoPreferenceId = mercadoPagoPreferenceId;
    }

    /**
     * Obtém o ID do pagamento no Mercado Pago.
     *
     * @return ID do pagamento ou null se ainda não foi processado
     */
    public Long getMercadoPagoPaymentId() {
        return mercadoPagoPaymentId;
    }

    /**
     * Define o ID do pagamento no Mercado Pago.
     *
     * @param mercadoPagoPaymentId ID do pagamento processado
     */
    public void setMercadoPagoPaymentId(Long mercadoPagoPaymentId) {
        this.mercadoPagoPaymentId = mercadoPagoPaymentId;
    }

    /**
     * Verifica se o pedido possui um link de pagamento gerado.
     *
     * @return true se o link existe, false caso contrário
     */
    public boolean hasCheckoutProLink() {
        return checkoutProLink != null && !checkoutProLink.trim().isEmpty();
    }

    /**
     * Verifica se o pedido foi pago (possui ID de pagamento).
     *
     * @return true se foi pago, false caso contrário
     */
    public boolean isPaid() {
        return mercadoPagoPaymentId != null && getStatus() == OrderStatus.PAGO;
    }
}