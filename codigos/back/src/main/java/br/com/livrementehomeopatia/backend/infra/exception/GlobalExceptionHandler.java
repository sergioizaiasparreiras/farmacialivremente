package br.com.livrementehomeopatia.backend.infra.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import java.util.stream.Collectors;

/**
 * Classe central para tratamento de exceções em toda a aplicação (AOP).
 * Captura exceções lançadas pelos controllers e as converte em respostas HTTP
 * padronizadas e informativas, além de registar os erros para depuração.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handler para a exceção MethodArgumentNotValidException, lançada quando um argumento
     * anotado com @Valid falha na validação.
     *
     * @param ex A exceção MethodArgumentNotValidException contendo os detalhes dos erros de validação.
     * @return Uma resposta HTTP 400 Bad Request com uma lista dos campos e mensagens de erro.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return "'" + fieldName + "': " + errorMessage;
                })
                .collect(Collectors.joining(", "));
        logger.warn("Erro de validação nos dados recebidos: {}", errors);
        return ResponseEntity.badRequest().body("Erro de validação: " + errors);
    }
    
    /**
     * Handler para a exceção DataIntegrityViolationException, tipicamente lançada
     * quando se tenta inserir um dado que viola uma restrição única no banco de dados
     * (ex: email ou CPF já existente).
     *
     * @param ex A exceção DataIntegrityViolationException.
     * @return Uma resposta HTTP 409 Conflict com uma mensagem amigável.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // Verifica se a causa é uma violação de chave única para dar uma mensagem mais específica.
        if (ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            logger.warn("Tentativa de inserir dado duplicado: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito de dados. O recurso que você está a tentar criar já existe (ex: email ou CPF duplicado).");
        }
        logger.error("Erro de integridade de dados não especificado:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro de integridade de dados.");
    }

    /**
     * Handler para a exceção BadCredentialsException, lançada pelo Spring Security
     * quando as credenciais de login (utilizador/senha) são inválidas.
     *
     * @return Uma resposta HTTP 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials() {
        logger.warn("Tentativa de login com credenciais inválidas.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
    }

    /**
     * Handler para outras exceções de autenticação genéricas.
     *
     * @return Uma resposta HTTP 401 Unauthorized.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationError() {
        logger.warn("Erro de autenticação genérico.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação.");
    }

    /**
     * Handler para a exceção AccessDeniedException, lançada quando um utilizador
     * autenticado tenta aceder a um recurso para o qual não tem a role necessária.
     *
     * @return Uma resposta HTTP 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied() {
        logger.warn("Tentativa de acesso negado a um recurso protegido.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
    }
    
    /**
     * Handler para a exceção EntityNotFoundException, lançada quando uma
     * operação de busca (ex: findById) não encontra o recurso.
     *
     * @return Uma resposta HTTP 404 Not Found.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound() {
        logger.info("Recurso não encontrado na base de dados.");
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Handler "catch-all" para qualquer outra exceção não tratada especificamente.
     * Garante que a aplicação nunca exponha stack traces ao cliente e regista o erro.
     *
     * @param ex A exceção que foi lançada.
     * @return Uma resposta HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericError(Exception ex) {
        // Regista a stack trace completa no log para depuração.
        logger.error("Erro inesperado e não tratado na aplicação:", ex);
        
        // Retorna uma mensagem genérica para o cliente.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno inesperado no servidor.");
    }
}
