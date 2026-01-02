package br.com.livrementehomeopatia.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.livrementehomeopatia.backend.model.Neighborhood;

public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Integer> {

    Optional<Neighborhood> findByName(String name);
    
}
