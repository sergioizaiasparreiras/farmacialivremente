package br.com.livrementehomeopatia.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.livrementehomeopatia.backend.dto.CartItemResponse;
import br.com.livrementehomeopatia.backend.services.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Adiciona um item ao carrinho do usuário autenticado.
     */
    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@RequestParam Integer productId) {
        try {
            cartService.addItemToCart(productId);
            List<CartItemResponse> updatedCart = cartService.getCartItemsForLoggedUser();
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Remove um item do carrinho do usuário autenticado.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeItemFromCart(@RequestParam Integer productId) {
        try {
            cartService.removeItemCompletelyFromCart(productId);
            List<CartItemResponse> updatedCart = cartService.getCartItemsForLoggedUser();
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Decrementa a quantidade de um item no carrinho do usuário autenticado.
     */
    @PostMapping("/decrease")
    public ResponseEntity<?> decreaseItemQuantity(@RequestParam Integer productId) {
        try {
            cartService.decreaseItemQuantityFromCart(productId);
            List<CartItemResponse> updatedCart = cartService.getCartItemsForLoggedUser();
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Retorna todos os itens do carrinho do usuário autenticado.
     */
    @GetMapping("/items")
    public ResponseEntity<?> getCartItems() {
        try {
            List<CartItemResponse> cartItems = cartService.getCartItemsForLoggedUser();
            return ResponseEntity.ok(cartItems);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Retorna o valor do carrinho atualmente do usuário autenticado.
     */
    @GetMapping("/totalValue")
    public ResponseEntity<?> getCartTotal() {
        try {
            Double total = cartService.getCartTotalValueForLoggedUser();
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }
}