package br.com.livrementehomeopatia.backend.infra.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.livrementehomeopatia.backend.model.User;

/**
 * Implementação customizada de UserDetails para integração com Spring Security.
 * Fornece os detalhes do usuário necessários para o processo de autenticação e autorização.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Constrói uma instância de CustomUserDetails com o usuário fornecido.
     *
     * @param user O usuário do sistema que será encapsulado
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Converte este CustomUserDetails em um objeto LoggedUser.
     *
     * @return Instância de LoggedUser contendo informações básicas do usuário
     */
    public LoggedUser toLoggedUser() {
        return new LoggedUser(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * Retorna as autoridades (roles) concedidas ao usuário.
     *
     * @return Coleção de autoridades (no formato ROLE_ + nome da role)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return A senha codificada do usuário
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Retorna o username do usuário (no caso, o email).
     *
     * @return O email do usuário que serve como identificador único
     */
    @Override
    public String getUsername() {
        return user.getEmail(); // importante para Spring Security
    }

    /**
     * Indica se a conta do usuário não está expirada.
     *
     * @return Sempre true (contas não expiram neste sistema)
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    /**
     * Indica se a conta do usuário não está bloqueada.
     *
     * @return Sempre true (contas não são bloqueadas neste sistema)
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indica se as credenciais do usuário não estão expiradas.
     *
     * @return Sempre true (credenciais não expiram neste sistema)
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indica se o usuário está habilitado.
     *
     * @return Sempre true (todos os usuários estão habilitados neste sistema)
     */
    @Override
    public boolean isEnabled() { return true; }

    /**
     * Retorna o objeto User original encapsulado por esta classe.
     *
     * @return O objeto User completo
     */
    public User getUser() {
        return user;
    }
}