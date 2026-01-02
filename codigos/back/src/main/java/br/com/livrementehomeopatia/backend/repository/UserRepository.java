package br.com.livrementehomeopatia.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository; // IMPORTANTE: para registrar como bean

import br.com.livrementehomeopatia.backend.enums.Role;
import br.com.livrementehomeopatia.backend.model.User;

/**
 * Repositório responsável pelo acesso aos dados da entidade {@link User}.
 * <p>
 * Estende {@link JpaRepository} para fornecer operações CRUD e paginação.
 */
@Repository // ESSENCIAL: Permite que o Spring reconheça e injete esse repositório
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /**
     * Busca um usuário pelo e-mail.
     *
     * @param email e-mail do usuário.
     * @return um {@link Optional} contendo o usuário, se encontrado.
     */
    Optional<User> findByEmail(String email);

    // Adicione esses métodos no UserRepository
    List<User> findByRoleNot(Role role);
    List<User> findByRoleNotAndFullNameLikeOrEmailLike(Role role, String fullName, String email);

    Optional<User> findById(Integer userId);

}
