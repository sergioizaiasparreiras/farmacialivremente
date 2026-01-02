package br.com.livrementehomeopatia.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CRMValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CRM {
    String message() default "CRM inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}