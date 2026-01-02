package br.com.livrementehomeopatia.backend.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Classe de configuração central para o Spring Security.
 * Define todas as regras de segurança web, incluindo CORS, CSRF,
 * gestão de sessão e autorização de endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    public SecurityConfig(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints Públicos
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/neighborhoods/**").permitAll()

                        // --- [CORREÇÃO] REGRAS DE PAGAMENTO (MERCADO PAGO) ---
                        // O webhook do Mercado Pago continua público.
                        .requestMatchers(HttpMethod.POST, "/api/pagamentos/notificacoes").permitAll()
                        // A criação de um pagamento agora é permitida para CLIENTES e MÉDICOS.
                        .requestMatchers(HttpMethod.POST, "/api/pagamentos/criar-preferencia").hasAnyRole("CLIENTE", "MEDICO") 
                        // ---------------------------------------------------


                        .requestMatchers(HttpMethod.GET, "/api/auth/check-crm").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/product/**").permitAll() // Qualquer pessoa pode ver produtos.
                        .requestMatchers(HttpMethod.GET, "/api/neighborhoods/**").permitAll() // Qualquer pessoa pode ver bairros.
                        

                        // Rotas de Produtos (Protegidas)
                        .requestMatchers(HttpMethod.POST, "/api/product").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/product/**").hasRole("ADMIN")
                        
                        // Rotas administrativas gerais
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Rotas de carrinho (clientes e médicos)
                        .requestMatchers("/api/cart/**").hasAnyRole("CLIENTE", "MEDICO")

                        // Rotas de pedidos
                        .requestMatchers(HttpMethod.GET, "/api/orders/myOrders").hasAnyRole("CLIENTE", "MEDICO")
                        .requestMatchers(HttpMethod.GET, "/api/orders/allOrders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("CLIENTE", "MEDICO")

                        // Rotas de endereços (Protegidas)
                        .requestMatchers(HttpMethod.POST,"/api/neighborhoods").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/neighborhoods/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/neighborhoods/**").hasRole("ADMIN")
                        
                        // Rotas de insumos (Protegidas)
                        .requestMatchers(HttpMethod.POST,"/api/inputs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/inputs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/inputs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/inputs/**").hasAnyRole("ADMIN", "MEDICO")

                        // Regra final: Qualquer outra requisição exige autenticação.
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://farmacialivremente.azurewebsites.net", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
