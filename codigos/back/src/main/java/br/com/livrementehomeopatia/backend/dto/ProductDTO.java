package br.com.livrementehomeopatia.backend.dto;

import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.livrementehomeopatia.backend.model.Product;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para representar um produto.
 */
@Getter
@Setter
public class ProductDTO {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private boolean available;
    private String type;
    private Set<String> categories;
    private String photo; // imagem em base64

    /**
     * Construtor que inicializa o DTO a partir de um objeto Product.
     *
     * @param product o produto a ser convertido
     */
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.available = product.isAvailable();
        this.type = product.getType().name();
        this.categories = product.getCategories().stream()
            .map(Enum::name)
            .collect(Collectors.toSet());

        if (product.getPhoto() != null) {
            this.photo = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(product.getPhoto());
        } else {
            this.photo = null;
        }
    }
}
