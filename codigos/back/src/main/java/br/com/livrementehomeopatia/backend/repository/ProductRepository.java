package br.com.livrementehomeopatia.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.livrementehomeopatia.backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String fullName);
    Optional<Product> findById(Integer id);
}
