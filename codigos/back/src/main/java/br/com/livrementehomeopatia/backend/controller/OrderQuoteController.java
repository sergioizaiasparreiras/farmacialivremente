package br.com.livrementehomeopatia.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.livrementehomeopatia.backend.dto.OrderQuoteDTO;
import br.com.livrementehomeopatia.backend.dto.AddressDTO;
import br.com.livrementehomeopatia.backend.services.OrderQuoteService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequestMapping("/api/order-quotes")
@RequiredArgsConstructor
public class OrderQuoteController {

    private final OrderQuoteService orderQuoteService;

    @PostMapping("/{orderId}")
    public ResponseEntity<OrderQuoteDTO> createOrderQuote(
            @PathVariable Integer orderId,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam(value = "observation", required = false) String observation,
            @RequestParam(value = "medicalPrescription", required = false) MultipartFile medicalPrescription,
            @RequestParam(value = "cep", required = false) String cep,
            @RequestParam(value = "rua", required = false) String rua,
            @RequestParam(value = "numero", required = false) Integer numero,
            @RequestParam(value = "complemento", required = false) String complemento) {
        try {
            OrderQuoteDTO quoteDTO = new OrderQuoteDTO();
            quoteDTO.setFullName(fullName);
            quoteDTO.setPhone(phone);
            quoteDTO.setEmail(email);
            quoteDTO.setObservation(observation);

            if (medicalPrescription != null && !medicalPrescription.isEmpty()) {
                try {
                    byte[] fileBytes = medicalPrescription.getBytes();
                    quoteDTO.setMedicalPrescriptionBase64(java.util.Base64.getEncoder().encodeToString(fileBytes));
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body(null);
                }
            }

            if (cep != null || rua != null || numero != null || complemento != null) {
                AddressDTO addressDTO = new AddressDTO();
                addressDTO.setCep(cep);
                addressDTO.setRua(rua);
                addressDTO.setNumero(numero);
                addressDTO.setComplemento(complemento);
                quoteDTO.setAddress(addressDTO);
            }

            OrderQuoteDTO createdQuote = orderQuoteService.createOrderQuote(quoteDTO, orderId);
            return ResponseEntity.ok(createdQuote);
        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{quoteId}")
    public ResponseEntity<OrderQuoteDTO> getOrderQuote(@PathVariable Integer quoteId) {
        try {
            OrderQuoteDTO quote = orderQuoteService.getOrderQuote(quoteId);
            return ResponseEntity.ok(quote);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 