package br.com.livrementehomeopatia.backend.model;

import java.util.Set;


import br.com.livrementehomeopatia.backend.enums.Categories;
import br.com.livrementehomeopatia.backend.enums.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa um produto.
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "photo")
    private byte[] photo;

    @Column(nullable = false)
    private boolean available;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    @ElementCollection(targetClass = Categories.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category")
    private Set<Categories> categories;

    /**
     * Construtor para inicializar um produto.
     *
     * @param id ID do produto
     * @param name nome do produto
     * @param description descrição do produto
     * @param price preço do produto
     * @param photo foto do produto
     * @param available disponibilidade do produto
     * @param type tipo do produto
     * @param categories categorias do produto
     */
    public Product(Integer id, String name, String description, Double price, byte[] photo, boolean available, ProductType type, Set<Categories> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.photo = photo;
        this.available = available;
        this.type = type;
        this.categories = categories;
    }
}
