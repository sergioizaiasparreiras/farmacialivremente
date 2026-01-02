package br.com.livrementehomeopatia.backend.dto;

import java.util.Base64;

import br.com.livrementehomeopatia.backend.enums.ProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {

    private Integer cartItemId;
    private Integer productId;
    private String productName;
    private int quantity;
    private Double productPrice;
    private ProductType productType;
    private String productPhotoBase64;

    public CartItemResponse(Integer cartItemId, Integer productId, String productName, int quantity, Double productPrice, ProductType productType,byte[] productPhoto) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.productType = productType;

        if (productPhoto != null) {
            this.productPhotoBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(productPhoto);
        }
    }
}