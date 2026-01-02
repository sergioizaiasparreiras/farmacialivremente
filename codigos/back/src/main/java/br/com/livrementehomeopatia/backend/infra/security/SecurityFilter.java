package br.com.livrementehomeopatia.backend.infra.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.livrementehomeopatia.backend.model.User;
import br.com.livrementehomeopatia.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro de segurança Spring que intercepta cada requisição para validar tokens JWT.
 * Estende OncePerRequestFilter para garantir uma única execução por requisição.
 */
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    /**
     * Serviço para operações relacionadas a tokens JWT (validação, geração, etc.)
     */
    private final TokenService tokenService;

    /**
     * Repositório para operações de banco de dados relacionadas a usuários
     */
    private final UserRepository userRepository;

    /**
     * Método principal que filtra cada requisição para autenticação via JWT.
     * 
     * @param request Objeto HttpServletRequest contendo os dados da requisição
     * @param response Objeto HttpServletResponse para a resposta
     * @param filterChain Cadeia de filtros para continuar o processamento da requisição
     * @throws ServletException Se ocorrer um erro no filtro
     * @throws IOException Se ocorrer um erro de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = this.recoverToken(request);

        if (token != null) {
            var login = tokenService.validateToken(token);
            
            User user = userRepository.findByEmail(login)
                    .orElseThrow(() -> new RuntimeException("User Not Found in token"));

            var loggedUser = new LoggedUser(
                    user.getId(),
                    user.getEmail(),
                    "ROLE_" + user.getRole().name()
            );

            var authorities = Collections.singletonList(new SimpleGrantedAuthority(loggedUser.getRole()));

            var authentication = new UsernamePasswordAuthenticationToken(loggedUser, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Recupera o token JWT do cabeçalho Authorization da requisição.
     * 
     * @param request Objeto HttpServletRequest contendo os cabeçalhos
     * @return O token JWT sem o prefixo "Bearer " ou null se não encontrado
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
