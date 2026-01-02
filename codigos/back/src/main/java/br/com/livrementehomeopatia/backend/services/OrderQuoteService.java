package br.com.livrementehomeopatia.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.livrementehomeopatia.backend.dto.OrderQuoteDTO;
import br.com.livrementehomeopatia.backend.dto.AddressDTO;
import br.com.livrementehomeopatia.backend.model.OrderQuote;
import br.com.livrementehomeopatia.backend.model.HomeophaticOrder;
import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.model.Address;
import br.com.livrementehomeopatia.backend.repository.OrderQuoteRepository;
import br.com.livrementehomeopatia.backend.repository.OrderRepository;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import br.com.livrementehomeopatia.backend.infra.security.LoggedUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderQuoteService {

    private final OrderQuoteRepository orderQuoteRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderQuoteDTO createOrderQuote(OrderQuoteDTO quoteDTO, Integer orderId) {
        try {
            HomeophaticOrder order = (HomeophaticOrder) orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

            User user = getAuthenticatedUser();

            OrderQuote quote = new OrderQuote();
            quote.setUser(user);
            quote.setFullName(quoteDTO.getFullName());
            quote.setPhone(quoteDTO.getPhone());
            quote.setEmail(quoteDTO.getEmail());
            quote.setOrder(order);
            quote.setObservation(quoteDTO.getObservation());

            if (quoteDTO.getMedicalPrescriptionBase64() != null && !quoteDTO.getMedicalPrescriptionBase64().isEmpty()) {
                try {
                    byte[] prescriptionBytes = Base64.getDecoder().decode(quoteDTO.getMedicalPrescriptionBase64().getBytes());
                    quote.setMedicalPrescription(prescriptionBytes);
                } catch (IllegalArgumentException e) {
                    log.error("Erro ao decodificar arquivo: ", e);
                    throw new RuntimeException("Erro ao processar arquivo da receita médica");
                }
            }

            if (quoteDTO.getAddress() != null) {
                Address address = new Address();
                address.setCep(quoteDTO.getAddress().getCep());
                address.setRua(quoteDTO.getAddress().getRua());
                address.setNumero(quoteDTO.getAddress().getNumero());
                address.setComplemento(quoteDTO.getAddress().getComplemento());
                quote.setAddress(address);
            }

            quote = orderQuoteRepository.save(quote);
            order.setOrderQuote(quote);
            order.getOrderQuote().setFullName(quote.getFullName());
            order.getOrderQuote().setPhone(quote.getPhone());
            order.getOrderQuote().setEmail(quote.getEmail());
            orderRepository.save(order);

            return convertToDTO(quote);
        } catch (Exception e) {
            log.error("Erro ao criar orçamento: ", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public OrderQuoteDTO getOrderQuote(Integer quoteId) {
        OrderQuote quote = orderQuoteRepository.findById(quoteId)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
        return convertToDTO(quote);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof LoggedUser)) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Integer userId = ((LoggedUser) auth.getPrincipal()).getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private OrderQuoteDTO convertToDTO(OrderQuote quote) {
        OrderQuoteDTO dto = new OrderQuoteDTO();
        dto.setId(quote.getId());
        dto.setFullName(quote.getFullName());
        dto.setPhone(quote.getPhone());
        dto.setEmail(quote.getEmail());
        dto.setObservation(quote.getObservation());
        
        if (quote.getMedicalPrescription() != null) {
            dto.setMedicalPrescriptionBase64(Base64.getEncoder().encodeToString(quote.getMedicalPrescription()));
        }
        
        if (quote.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setCep(quote.getAddress().getCep());
            addressDTO.setRua(quote.getAddress().getRua());
            addressDTO.setNumero(quote.getAddress().getNumero());
            addressDTO.setComplemento(quote.getAddress().getComplemento());
            dto.setAddress(addressDTO);
        }
        
        return dto;
    }
} 