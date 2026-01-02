package br.com.livrementehomeopatia.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.livrementehomeopatia.backend.model.Client;

/**
 * Repositório responsável pelo acesso aos dados da entidade {@link Doctor}.
 * <p>
 * Estende {@link JpaRepository} para fornecer operações CRUD e paginação.
 */
public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * Busca um cliente pelo e-mail.
     *
     * @param email e-mail do cliente.
     * @return um {@link Optional} contendo o cliente, se encontrado.
     */
    Optional<Client> findByEmail(String email);
}
