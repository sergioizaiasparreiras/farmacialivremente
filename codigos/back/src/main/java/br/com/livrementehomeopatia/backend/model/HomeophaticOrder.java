package br.com.livrementehomeopatia.backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import br.com.livrementehomeopatia.backend.enums.OrderStatus;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "homeopathic_orders")
@Getter
@Setter
public class HomeophaticOrder extends Order {
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_quote_id", referencedColumnName = "id")
    private OrderQuote orderQuote;

    public HomeophaticOrder() {
        super();
    }

    public HomeophaticOrder(User user, Neighborhood neighborhood, Double deliveryTax, LocalDateTime createdAt, OrderStatus status, OrderQuote orderQuote) {
        super(user, neighborhood, deliveryTax, createdAt, status);
        this.orderQuote = orderQuote;
    }

}
