package br.com.livrementehomeopatia.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.livrementehomeopatia.backend.dto.CartItemResponse;
import br.com.livrementehomeopatia.backend.dto.CartItemWithoutProductPhoto;
import br.com.livrementehomeopatia.backend.infra.security.LoggedUser;
import br.com.livrementehomeopatia.backend.model.Cart;
import br.com.livrementehomeopatia.backend.model.CartItem;

import br.com.livrementehomeopatia.backend.model.Product;
import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.repository.CartItemRepository;
import br.com.livrementehomeopatia.backend.repository.CartRepository;

import br.com.livrementehomeopatia.backend.repository.ProductRepository;
import br.com.livrementehomeopatia.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Adiciona um item ao carrinho do usuário autenticado.
     *
     * @param productId o id do produto a ser adicionado
     */
    @Transactional
    public void addItemToCart(Integer productId) {
        LoggedUser loggedUser = getLoggedUser();
        Integer userId = loggedUser.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Cart cart = getOrCreateCart(user);

        Product productToAdd = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        verificarCompatibilidadeDeTipoNoCarrinho(cart, productToAdd);

        Optional<CartItemWithoutProductPhoto> optionalItem = cartItemRepository.findCartItemWithoutPhoto(userId,
                productId);

        if (optionalItem.isPresent()) {
            CartItemWithoutProductPhoto dto = optionalItem.get();
            CartItem existingItem = cartItemRepository.findById(dto.getCartItemId())
                    .orElseThrow(() -> new RuntimeException("Item do carrinho não encontrado"));
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setUser(user);
            newItem.setProduct(productToAdd);
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
        }

        updateCartTotal(cart);
    }

    /**
     * Remove um item do carrinho do usuário autenticado, completamente.
     *
     * @param productId o id do produto a ser removido
     */
    @Transactional
    public void removeItemCompletelyFromCart(Integer productId) {
        LoggedUser loggedUser = getLoggedUser();
        Integer userId = loggedUser.getId();

        Optional<CartItemWithoutProductPhoto> optionalItem = cartItemRepository.findCartItemWithoutPhoto(userId,
                productId);

        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Item não encontrado no carrinho");
        }

        CartItemWithoutProductPhoto dto = optionalItem.get();
        CartItem item = cartItemRepository.findById(dto.getCartItemId())
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        Cart cart = item.getCart();

        cartItemRepository.delete(item);

        updateCartTotal(cart);
    }

    /**
     * Decrementa a quantidade de um item no carrinho do usuario autenticado.
     *
     * @param productId o id do produto a ter a quantidade decrementada
     */
    @Transactional
    public void decreaseItemQuantityFromCart(Integer productId) {
        LoggedUser loggedUser = getLoggedUser();
        Integer userId = loggedUser.getId();

        Optional<CartItemWithoutProductPhoto> optionalItem = cartItemRepository.findCartItemWithoutPhoto(userId,
                productId);

        if (optionalItem.isEmpty()) {
            throw new RuntimeException("Item não encontrado no carrinho");
        }

        CartItemWithoutProductPhoto dto = optionalItem.get();
        CartItem item = cartItemRepository.findById(dto.getCartItemId())
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        Cart cart = item.getCart();

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);
        } else {

            item.setQuantity(1);
            cartItemRepository.save(item);
        }

        updateCartTotal(cart);
    }

    /**
     * Retorna todos os itens do carrinho do usuario autenticado.
     * 
     * @return uma lista de itens do carrinho
     */
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItemsForLoggedUser() {
        LoggedUser loggedUser = getLoggedUser();
        return cartItemRepository.findAllItemsByUserId(loggedUser.getId());
    }

    /**
     * Retorna o valor total do carrinho do usuario autenticado.
     * 
     * @return o valor total do carrinho
     */
    @Transactional(readOnly = true)
    public Double getCartTotalValueForLoggedUser() {
        LoggedUser loggedUser = getLoggedUser();
        Integer userId = loggedUser.getId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        return cart.getTotalValue();
    }

    /**
     * Atualiza o valor total do carrinho passado como parâmetro.
     * 
     * @param cart o carrinho a ter o valor total atualizado
     */
    private void updateCartTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());

        double total = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        cart.setTotalValue(total);
        cartRepository.save(cart);
    }

    /**
     * Retorna o objeto LoggedUser que representa o usuário autenticado.
     * 
     * @return o objeto LoggedUser com informações do usuário autenticado
     * @throws RuntimeException caso o usuário não esteja autenticado
     */
    private LoggedUser getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof LoggedUser)) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return (LoggedUser) auth.getPrincipal();
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

/**
* Verifica a compatibilidade do tipo de produto que está sendo adicionado ao carrinho.
* 
* Este método verifica se o tipo do produto a ser adicionado entra em conflito com
* os tipos de produtos já presentes no carrinho. Ele garante que produtos
* de tipos diferentes (por exemplo, homeopático e revenda) não sejam misturados no mesmo carrinho.
* 
* @param cart o carrinho ao qual o produto está sendo adicionado
* @param productToAdd o produto cujo tipo está sendo verificado para compatibilidade
* @throws RuntimeException se houver tentativa de misturar produtos de tipos diferentes
*/
    private void verificarCompatibilidadeDeTipoNoCarrinho(Cart cart, Product productToAdd) {
        String tipoNovoProduto = productToAdd.getType().toString();

        List<CartItem> itensExistentes = cartItemRepository.findAllByCartId(cart.getId());

        boolean existeTipoDiferente = itensExistentes.stream()
                .map(item -> item.getProduct().getType().toString())
                .anyMatch(tipoExistente -> !tipoExistente.equals(tipoNovoProduto));

        if (existeTipoDiferente) {
            throw new RuntimeException("Não é possível misturar produtos homeopáticos com medicamentos de revenda no mesmo carrinho. Finalize a compra atual ou esvazie o carrinho para adicionar produtos de outro tipo.");
        }
    }
}