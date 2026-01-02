package br.com.livrementehomeopatia.backend.repository;

import br.com.livrementehomeopatia.backend.model.ResaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações com a entidade ResaleOrder.
 * <p>
 * Fornece métodos para buscar pedidos de revenda por diferentes critérios,
 * incluindo ID de pagamento e preferência do Mercado Pago.
 * </p>
 *
 * @author Sistema Farmácia Livremente
 * @version 2.0 - Refatorada para Checkout Pro
 * @see ResaleOrder
 * @see JpaRepository
 */
@Repository
public interface ResaleOrderRepository extends JpaRepository<ResaleOrder, Integer> {

    /**
     * Busca um pedido de revenda pelo ID do pagamento no Mercado Pago.
     * <p>
     * Usado principalmente pelo webhook para identificar qual pedido foi pago
     * quando recebe uma notificação de pagamento aprovado.
     * </p>
     *
     * @param paymentId ID do pagamento no Mercado Pago
     * @return Optional contendo o pedido se encontrado
     */
    Optional<ResaleOrder> findByMercadoPagoPaymentId(Long paymentId);

    /**
     * Busca um pedido de revenda pelo ID da preferência no Mercado Pago.
     * <p>
     * Útil para rastrear pedidos pela preferência criada no Checkout Pro.
     * </p>
     *
     * @param preferenceId ID da preferência no Mercado Pago
     * @return Optional contendo o pedido se encontrado
     */
    Optional<ResaleOrder> findByMercadoPagoPreferenceId(String preferenceId);

    /**
     * Busca pedidos de revenda que ainda não possuem link do Checkout Pro.
     * <p>
     * Útil para identificar pedidos que precisam ter seus links de pagamento gerados.
     * </p>
     *
     * @return Lista de pedidos sem link de pagamento
     */
    @Query("SELECT r FROM ResaleOrder r WHERE r.checkoutProLink IS NULL OR r.checkoutProLink = ''")
    java.util.List<ResaleOrder> findOrdersWithoutCheckoutProLink();

    /**
     * Busca pedidos de revenda aguardando pagamento.
     * <p>
     * Retorna pedidos com status AGUARDANDO_PAGAMENTO que possuem link do Checkout Pro.
     * </p>
     *
     * @return Lista de pedidos aguardando pagamento
     */
    @Query("SELECT r FROM ResaleOrder r WHERE r.status = 'AGUARDANDO_PAGAMENTO' AND r.checkoutProLink IS NOT NULL")
    java.util.List<ResaleOrder> findPendingPaymentOrders();
}