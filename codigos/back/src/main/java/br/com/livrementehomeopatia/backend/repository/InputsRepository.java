package br.com.livrementehomeopatia.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.livrementehomeopatia.backend.model.Inputs;

public interface InputsRepository extends JpaRepository<Inputs, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
