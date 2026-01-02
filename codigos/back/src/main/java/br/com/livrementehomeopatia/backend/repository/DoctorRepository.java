package br.com.livrementehomeopatia.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


import br.com.livrementehomeopatia.backend.model.Doctor;

/**
 * Repositório responsável pelo acesso aos dados da entidade {@link Doctor}.
 * <p>
 * Estende {@link JpaRepository} para fornecer operações CRUD e paginação.
 */
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    /**
     * Busca um médico pelo e-mail.
     *
     * @param email e-mail do médico.
     * @return um {@link Optional} contendo o médico, se encontrado.
     */
    Optional<Doctor> findByEmail(String email);
    boolean existsByCrm(String crm);
}
