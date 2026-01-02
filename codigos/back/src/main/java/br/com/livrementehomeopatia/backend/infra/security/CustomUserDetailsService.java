package br.com.livrementehomeopatia.backend.infra.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Implementação personalizada de UserDetailsService para integração com Spring Security.
 * <p>
 * Responsável por carregar os detalhes do usuário durante o processo de autenticação,
 * convertendo a entidade User em um objeto UserDetails que o Spring Security pode entender.
 */
@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    /**
     * Carrega os detalhes do usuário pelo email (username) fornecido.
     * 
     * @param username O email do usuário a ser autenticado (funciona como username no sistema)
     * @return UserDetails contendo informações necessárias para autenticação
     * @throws UsernameNotFoundException Se nenhum usuário for encontrado com o email fornecido
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));
        
        return new CustomUserDetails(user);
    }
}