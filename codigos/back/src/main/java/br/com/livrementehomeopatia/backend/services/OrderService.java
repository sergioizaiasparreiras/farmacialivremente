package br.com.livrementehomeopatia.backend.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import br.com.livrementehomeopatia.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.livrementehomeopatia.backend.dto.*;
import br.com.livrementehomeopatia.backend.enums.OrderStatus;
import br.com.livrementehomeopatia.backend.enums.ProductType;
import br.com.livrementehomeopatia.backend.infra.security.LoggedUser;
import br.com.livrementehomeopatia.backend.infra.mercadopago.dto.PaymentItemDTO;
import br.com.livrementehomeopatia.backend.infra.mercadopago.dto.PreferenceRequestDTO;
import br.com.livrementehomeopatia.backend.infra.mercadopago.dto.PreferenceResponseDTO;
import br.com.livrementehomeopatia.backend.infra.mercadopago.service.MercadoPagoService;
import br.com.livrementehomeopatia.backend.model.*;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por gerenciar pedidos, incluindo criação, recuperação
 * e processamento de pedidos homeopáticos e de revenda.
 * <p>
 * Delega a lógica de pagamento de pedidos de revenda para o {@link MercadoPagoService}.
 * </p>
 *
 * @author Sistema Farmácia Livremente
 * @version 2.2 - Integração Final e Corrigida
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final ResaleOrderRepository resaleOrderRepository;
    private final MercadoPagoService mercadoPagoService;

    /**
     * DTO de Resposta para a criação de um pedido de revenda, contendo os detalhes do pedido e a URL de pagamento.
     */
    public record ResaleOrderCreationResponse(OrderDTO orderDetails, String paymentUrl) {}

    /**
     * Cria um novo pedido a partir do carrinho do usuário.
     * Se for um pedido de revenda, ele delega a criação do pagamento ao MercadoPagoService
     * e retorna um ResaleOrderCreationResponse. Caso contrário, retorna um OrderDTO padrão.
     *
     * @param neighborhoodName O nome do bairro para entrega.
     * @return Um objeto contendo os dados do pedido (o tipo varia conforme a lógica).
     */
    @Transactional
    public Object createOrderFromCart(String neighborhoodName) {
        log.info("Iniciando criação de pedido a partir do carrinho para bairro: {}", neighborhoodName);

        User user = getAuthenticatedUser();
        Neighborhood neighborhood = getNeighborhoodByName(neighborhoodName);
        Cart cart = getValidUserCart(user.getId());

        validateCartContents(cart);

        ProductType cartType = determineCartProductType(cart);
        Order order = createOrderBasedOnType(cartType);

        configureOrderBasics(order, user, neighborhood, cartType);
        processOrderItems(order, cart);

        order = orderRepository.save(order);
        clearUserCart(cart);

        if (order instanceof ResaleOrder) {
            log.info("Pedido de revenda ID: {} criado. Gerando link de pagamento...", order.getId());
            
            var paymentItems = order.getItems().stream()
                .map(item -> new PaymentItemDTO(
                    item.getProduct().getName(), 
                    item.getProduct().getDescription(), 
                    item.getQuantity(), 
                    BigDecimal.valueOf(item.getPrice())))
                .collect(Collectors.toList());

            // Adicionar o frete como um item separado
            if (order.getDeliveryTax() > 0) {
                paymentItems.add(new PaymentItemDTO(
                    "Frete - " + order.getNeighborhood().getName(),
                    "Taxa de entrega para " + order.getNeighborhood().getName(),
                    1,
                    BigDecimal.valueOf(order.getDeliveryTax())
                ));
            }

            var preferenceRequest = new PreferenceRequestDTO(paymentItems, order.getId().toString());

            try {
                PreferenceResponseDTO paymentResponse = mercadoPagoService.createPaymentPreference(preferenceRequest);
                return new ResaleOrderCreationResponse(convertToDTO(order), paymentResponse.initPoint());
            } catch (Exception e) {
                log.error("Falha crítica ao criar preferência de pagamento para o pedido {}. Revertendo transação.", order.getId(), e);
                throw new RuntimeException("Não foi possível gerar o link de pagamento. Tente novamente.", e);
            }
        }

        log.info("Pedido homeopático criado com sucesso. ID: {}", order.getId());
        return convertToDTO(order);
    }
    
    // --- MÉTODOS OBSOLETOS REMOVIDOS ---
    // A lógica anterior de `saveCheckoutProLink`, `markResaleOrderAsPaidByPaymentId`,
    // e `associatePaymentId` foi removida pois agora está centralizada no MercadoPagoService.

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public ResaleOrder getResaleOrderById(Integer orderId) {
        return resaleOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido de revenda não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrderHistoryForUser() {
        User user = getAuthenticatedUser();
        return orderRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrdersForAdmin() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Pedido não encontrado");
        }
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void deleteHomeopathicOrderForUser(Integer orderId) {
        User user = getAuthenticatedUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Você não tem permissão para excluir este pedido");
        }
        
        if (!(order instanceof HomeophaticOrder)) {
            throw new RuntimeException("Apenas pedidos homeopáticos podem ser excluídos por esta operação");
        }
        
        orderRepository.delete(order);
    }
    
    @Transactional(readOnly = true)
    public Order getOrderEntityById(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof LoggedUser)) {
            throw new RuntimeException("Usuário não autenticado");
        }
        Integer userId = ((LoggedUser) auth.getPrincipal()).getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private Neighborhood getNeighborhoodByName(String name) {
        return neighborhoodRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));
    }

    private Cart getValidUserCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Carrinho está vazio");
        }
        return cart;
    }

    private void validateCartContents(Cart cart) {
        long distinctProductTypes = cart.getItems().stream()
                .map(item -> item.getProduct().getType())
                .distinct()
                .count();
        if (distinctProductTypes > 1) {
            throw new RuntimeException("O carrinho não pode conter produtos homeopáticos e de revenda ao mesmo tempo");
        }
    }

    private ProductType determineCartProductType(Cart cart) {
        return cart.getItems().get(0).getProduct().getType();
    }

    private Order createOrderBasedOnType(ProductType type) {
        return type == ProductType.HOMEOPATICO ? new HomeophaticOrder() : new ResaleOrder();
    }

    private void configureOrderBasics(Order order, User user, Neighborhood neighborhood, ProductType productType) {
        order.setUser(user);
        order.setNeighborhood(neighborhood);
        order.setDeliveryTax(neighborhood.getTax());
        order.setCreatedAt(LocalDateTime.now());
        
        if (productType == ProductType.REVENDA) {
    order.setStatus(OrderStatus.PAGO); // <-- já marca como pago!
} else {
    order.setStatus(OrderStatus.EM_ANDAMENTO);
}
    }

    private void processOrderItems(Order order, Cart cart) {
        double subtotal = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : new ArrayList<>(cart.getItems())) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            subtotal += cartItem.getQuantity() * cartItem.getProduct().getPrice();
        }
        order.setItems(orderItems);
        order.setTotalValue(subtotal + order.getDeliveryTax());
    }

    private void clearUserCart(Cart cart) {
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalValue(0.0);
        cartRepository.save(cart);
    }
    
    // --- LÓGICA DE CONVERSÃO PARA DTO (RESTAURADA E COMPLETA) ---
    
    private OrderDTO convertToDTO(Order order) {
        if (order instanceof HomeophaticOrder homeophaticOrder) {
            HomeophaticOrderDTO dto = new HomeophaticOrderDTO();
            setCommonOrderFields(order, dto);
            dto.setOrderQuote(convertQuoteToDTO(homeophaticOrder.getOrderQuote()));
            
            OrderQuote orderQuote = ((HomeophaticOrder) order).getOrderQuote();
            if (orderQuote != null) {
                dto.setOrderQuote(convertQuoteToDTO(orderQuote));
            }
            
            return dto;
        } else if (order instanceof ResaleOrder resaleOrder) {
            ResaleOrderDTO dto = new ResaleOrderDTO();
            setCommonOrderFields(order, dto);
            dto.setCheckoutProLink(resaleOrder.getCheckoutProLink());
            dto.setMercadoPagoPreferenceId(resaleOrder.getMercadoPagoPreferenceId());
            return dto;
        } else {
            throw new RuntimeException("Tipo de pedido desconhecido: " + order.getClass().getName());
        }
    }

    private void setCommonOrderFields(Order order, OrderDTO orderDTO) {
        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getCreatedAt());
        orderDTO.setItems(convertOrderItemsToDTOs(order.getItems()));
        orderDTO.setTotalValue(order.getTotalValue() - order.getDeliveryTax());
        orderDTO.setDeliveryTax(order.getDeliveryTax());
        orderDTO.setTotalWithDelivery(order.getTotalValue());
        orderDTO.setStatus(order.getStatus().name());
        orderDTO.setFullName(order.getUser().getFullName());
        orderDTO.setNeighborhoodName(order.getNeighborhood() != null ? order.getNeighborhood().getName() : null);
        orderDTO.setOrderType(determineOrderType(order));
    }
    

    /**
     * Converte uma entidade OrderQuote para seu DTO.
     *
     * @param quote entidade OrderQuote a ser convertida
     * @return OrderQuoteDTO convertido ou null se quote for null
     */
    private OrderQuoteDTO convertQuoteToDTO(OrderQuote quote) {
        if (quote == null) {
            return null;
        }
        
        OrderQuoteDTO quoteDTO = new OrderQuoteDTO();
        quoteDTO.setId(quote.getId());
        quoteDTO.setFullName(quote.getFullName());
        quoteDTO.setPhone(quote.getPhone());
        quoteDTO.setEmail(quote.getEmail());
        quoteDTO.setObservation(quote.getObservation());

        if (quote.getMedicalPrescription() != null) {
            quoteDTO.setMedicalPrescriptionBase64(Base64.getEncoder().encodeToString(quote.getMedicalPrescription()));
        }

        if (quote.getAddress() != null) {
            quoteDTO.setAddress(convertAddressToDTO(quote.getAddress()));
        }

        return quoteDTO;
    }
    
    private AddressDTO convertAddressToDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCep(address.getCep());
        addressDTO.setRua(address.getRua());
        addressDTO.setNumero(address.getNumero());
        addressDTO.setComplemento(address.getComplemento());
        return addressDTO;
    }
    
    private List<OrderItemDTO> convertOrderItemsToDTOs(List<OrderItem> items) {
        return items.stream().map(this::convertOrderItemToDTO).toList();
    }
    
    private OrderItemDTO convertOrderItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        byte[] photoBytes = item.getProduct().getPhoto();
        if (photoBytes != null && photoBytes.length > 0) {
            dto.setProductImageBase64(Base64.getEncoder().encodeToString(photoBytes));
        }
        return dto;
    }
    
    private ProductType determineOrderType(Order order) {
        if (order instanceof HomeophaticOrder) {
            return ProductType.HOMEOPATICO;
        } else if (order instanceof ResaleOrder) {
            return ProductType.REVENDA;
        }
        throw new RuntimeException("Tipo de pedido desconhecido");
    }
}