package br.com.livrementehomeopatia.backend.infra.admin.repository;

import br.com.livrementehomeopatia.backend.infra.admin.model.Admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AdminRepository extends JpaRepository<Admin, Integer> {
    // Remova ou substitua este método:
    // Optional<Admin> findAllAdmins();
    
    // Por um destes:
    List<Admin> findAll(); // Para buscar todos os admins
    
    // Ou se precisar de algo específico:
    Optional<Admin> findByEmail(String email); // Para buscar por email
}