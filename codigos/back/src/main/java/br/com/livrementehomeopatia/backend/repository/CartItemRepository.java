package br.com.livrementehomeopatia.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.livrementehomeopatia.backend.dto.CartItemResponse;
import br.com.livrementehomeopatia.backend.dto.CartItemWithoutProductPhoto;
import br.com.livrementehomeopatia.backend.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
        @Query("SELECT new br.com.livrementehomeopatia.backend.dto.CartItemWithoutProductPhoto(ci.id, ci.quantity, ci.product.id, ci.user.id) "
                        +
                        "FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId")
        Optional<CartItemWithoutProductPhoto> findCartItemWithoutPhoto(@Param("userId") Integer userId,
                        @Param("productId") Integer productId);

        @Query("SELECT new br.com.livrementehomeopatia.backend.dto.CartItemResponse(" +
                        "ci.id, ci.product.id, ci.product.name, ci.quantity, ci.product.price, ci.product.type, ci.product.photo) " +
                        "FROM CartItem ci WHERE ci.user.id = :userId")
        List<CartItemResponse> findAllItemsByUserId(@Param("userId") Integer userId);

        @Modifying
        @Transactional
        @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
        void deleteAllByCartId(@Param("cartId") Integer cartId);

        @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
        List<CartItem> findAllByCartId(@Param("cartId") Integer cartId);

}