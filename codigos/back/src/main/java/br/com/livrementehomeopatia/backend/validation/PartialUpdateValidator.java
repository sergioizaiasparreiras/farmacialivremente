package br.com.livrementehomeopatia.backend.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Componente responsável por realizar validações parciais em DTOs.
 * Utiliza o grupo de validação {@link OnFieldPresent} para aplicar regras apenas aos campos presentes.
 * 
 * Ideal para operações de atualização parcial (como PATCH), onde apenas os campos enviados devem ser validados.
 */
@Component
@RequiredArgsConstructor
public class PartialUpdateValidator {

    private final Validator validator;

    /**
     * Valida os campos presentes no DTO com base no grupo {@link OnFieldPresent}.
     * Lança uma exceção com a primeira mensagem de erro encontrada, caso existam violações.
     *
     * @param dto objeto a ser validado
     * @param <T> tipo genérico do DTO
     * @throws IllegalArgumentException se alguma violação de validação for encontrada
     */
    public <T> void validatePartial(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto, OnFieldPresent.class);

        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }
    }
}
