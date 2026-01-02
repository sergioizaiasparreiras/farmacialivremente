package br.com.livrementehomeopatia.backend.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.livrementehomeopatia.backend.dto.UserUpdateDTO;
import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Componente auxiliar para aplicar atualizações no objeto {@link User} com base em um DTO.
 * Realiza validações como checagem de e-mail duplicado e codificação de senha.
 */
@RequiredArgsConstructor
@Component
public class UserUpdateHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Aplica atualizações nos dados de um usuário com base nas informações fornecidas.
     * Apenas campos não nulos e não em branco são atualizados. Também garante que o novo e-mail (se alterado) seja único.
     *
     * @param user o usuário a ser atualizado
     * @param dto os novos dados do usuário encapsulados em um {@link UserUpdateDTO}
     * @throws RuntimeException se o e-mail informado já estiver em uso por outro usuário
     */
    public void applyUserUpdates(User user, UserUpdateDTO dto) {

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName().trim());
        }

        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            user.setPhone(dto.getPhone().trim());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = dto.getEmail().trim();

            // Só valida duplicidade se o novo email for diferente do atual
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                Optional<User> existing = userRepository.findByEmail(newEmail);
                if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
                    throw new RuntimeException("Este e-mail já está em uso por outro usuário.");
                }
                user.setEmail(newEmail);
            }
        }

        // A senha é OPCIONAL
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        }
    }
}
