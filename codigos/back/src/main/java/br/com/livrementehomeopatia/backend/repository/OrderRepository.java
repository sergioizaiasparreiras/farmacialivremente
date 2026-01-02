package br.com.livrementehomeopatia.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.livrementehomeopatia.backend.model.Order;
import br.com.livrementehomeopatia.backend.model.HomeophaticOrder;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    List<HomeophaticOrder> findHomeophaticOrdersByUserId(Integer userId);

}