package br.com.livrementehomeopatia.backend.dto;

public class CartItemWithoutProductPhoto {
    private Integer cartItemId;
    private Integer quantity;
    private Integer productId;
    private Integer userId;

    public CartItemWithoutProductPhoto(Integer cartItemId, Integer quantity, Integer productId, Integer userId) {
        this.cartItemId = cartItemId;
        this.quantity = quantity;
        this.productId = productId;
        this.userId = userId;
    }

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
