package br.com.livrementehomeopatia.backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.com.livrementehomeopatia.backend.dto.OrderDTO;
import br.com.livrementehomeopatia.backend.enums.OrderStatus;
import br.com.livrementehomeopatia.backend.model.Order;
import br.com.livrementehomeopatia.backend.services.OrderService;
import br.com.livrementehomeopatia.backend.services.MailService;
import lombok.RequiredArgsConstructor;

/**
 * Controller responsável por operações de pedidos (Order).
 * <p>
 * Gerencia criação, consulta e atualização de pedidos.
 * </p>
 *
 * @author Sistema Farmácia Livremente
 * @version 2.1
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    private final MailService mailService;

    /**
     * Cria um novo pedido a partir do carrinho do usuário autenticado.
     *
     * @param body Corpo da requisição contendo o nome do bairro
     * @return Um ResponseEntity contendo o resultado da criação do pedido.
     * Para pedidos de revenda, retorna um {@link OrderService.ResaleOrderCreationResponse}.
     * Para outros pedidos, retorna um {@link OrderDTO}.
     */
    @PostMapping("/create-from-cart")
    public ResponseEntity<?> createOrderFromCart(@RequestBody Map<String, String> body) {
        try {
            String neighborhoodName = body.get("neighborhood");
            
            if (neighborhoodName == null || neighborhoodName.trim().isEmpty()) {
                log.warn("Tentativa de criar pedido sem especificar bairro");
                return ResponseEntity.badRequest().body(Map.of("error", "O nome do bairro é obrigatório."));
            }
            
            log.info("Processando criação de pedido para o bairro: {}", neighborhoodName);
            Object result = orderService.createOrderFromCart(neighborhoodName);

            // [LÓGICA CORRIGIDA] Lida com os diferentes tipos de retorno do serviço.
            if (result instanceof OrderService.ResaleOrderCreationResponse response) {
                // É um pedido de revenda com link de pagamento
                log.info("Pedido de revenda ID {} criado. Retornando URL de pagamento.", response.orderDetails().getId());
                // Enviar e-mail de notificação para a farmácia
                Order order = orderService.getOrderEntityById(response.orderDetails().getId());
                mailService.sendOrderNotificationToPharmacy(order);
                return ResponseEntity.ok(response);

            } else if (result instanceof OrderDTO orderDTO) {
                // É um pedido homeopático normal
                log.info("Pedido homeopático ID {} criado com sucesso.", orderDTO.getId());
                // Enviar e-mail de notificação para a farmácia
                Order order = orderService.getOrderEntityById(orderDTO.getId());
                mailService.sendOrderNotificationToPharmacy(order);
                return ResponseEntity.ok(orderDTO);

            } else {
                // Caso inesperado
                log.error("O serviço de criação de pedido retornou um tipo de objeto inesperado: {}", result.getClass().getName());
                return ResponseEntity.internalServerError().build();
            }

        } catch (RuntimeException e) {
            log.error("Erro ao criar pedido: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- OUTROS MÉTODOS DO CONTROLLER (sem alterações) ---

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer orderId) {
        try {
            log.debug("Buscando pedido por ID: {}", orderId);
            OrderDTO orderDTO = orderService.getOrderById(orderId);
            return ResponseEntity.ok(orderDTO);
        } catch (RuntimeException e) {
            log.warn("Pedido não encontrado. ID: {}", orderId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/myOrders")
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        try {
            log.debug("Recuperando histórico de pedidos do usuário autenticado");
            List<OrderDTO> orders = orderService.getOrderHistoryForUser();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            log.error("Erro ao recuperar histórico de pedidos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/allOrders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        try {
            log.debug("Admin recuperando todos os pedidos do sistema");
            List<OrderDTO> orders = orderService.getAllOrdersForAdmin();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            log.error("Erro ao recuperar todos os pedidos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status-list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, String>>> getOrderStatusList() {
        List<Map<String, String>> statusList = 
            Arrays.stream(OrderStatus.values())
                .map(status -> Map.of(
                    "name", status.name(),
                    "descricao", status.getDescricao()
                ))
                .toList();
        return ResponseEntity.ok(statusList);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Integer orderId,
            @RequestParam OrderStatus status) {
        try {
            log.info("Admin atualizando status do pedido ID: {} para: {}", orderId, status);
            orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erro ao atualizar status do pedido ID: {} - {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{orderId}/homeopathic")
    public ResponseEntity<Void> deleteHomeopathicOrder(@PathVariable Integer orderId) {
        try {
            orderService.deleteHomeopathicOrderForUser(orderId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer orderId) {
        try {
            log.info("Admin removendo pedido ID: {}", orderId);
            orderService.deleteOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erro ao remover pedido ID: {} - {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}