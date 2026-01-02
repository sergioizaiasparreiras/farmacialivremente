package br.com.livrementehomeopatia.backend.infra.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import br.com.livrementehomeopatia.backend.dto.UserDTO;
import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.enums.Role;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;

@Service
public class AdminService {
    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lista usuários não-admins com filtro
    public Page<UserDTO> listRegularUsers(String filter, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> {
            Predicate notAdmin = cb.notEqual(root.get("role"), Role.ADMIN);

            if (filter == null || filter.isEmpty()) {
                return notAdmin;
            }

            String searchTerm = "%" + filter.toLowerCase() + "%";
            Predicate filterPredicate = cb.or(
                cb.like(cb.lower(root.get("fullName")), searchTerm),
                cb.like(cb.lower(root.get("email")), searchTerm)
            );

            return cb.and(notAdmin, filterPredicate);
        };

        return userRepository.findAll(spec, pageable).map(this::convertToDto);
    }

    // Busca usuário por ID
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return convertToDto(user);
    }

    // Ativa/desativa usuário
    @Transactional
    public void setUserActiveStatus(Integer id, boolean active) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (user.getRole() == Role.ADMIN) {
            throw new AccessDeniedException("Não é possível desativar administradores");
        }
        user.setActive(active);
        userRepository.save(user);
    }

    // Atualiza role do usuário
    @Transactional
    public void updateUserRole(Integer id, String newRole) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (user.getRole() == Role.ADMIN) {
            throw new AccessDeniedException("Não é possível alterar permissões de administradores");
        }
        user.setRole(Role.valueOf(newRole));
        userRepository.save(user);
    }

    // Conversão para DTO
    private UserDTO convertToDto(User user) {
        return new UserDTO(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedDate(),
            user.isActive()
        );
    }
}