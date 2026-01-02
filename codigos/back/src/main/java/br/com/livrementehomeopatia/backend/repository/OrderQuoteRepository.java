package br.com.livrementehomeopatia.backend.repository;

import br.com.livrementehomeopatia.backend.model.OrderQuote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderQuoteRepository extends JpaRepository<OrderQuote, Integer> {

    @Override
    Optional<OrderQuote> findById(Integer integer);
}
