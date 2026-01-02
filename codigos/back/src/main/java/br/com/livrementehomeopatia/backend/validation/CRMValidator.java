package br.com.livrementehomeopatia.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CRMValidator implements ConstraintValidator<CRM, String> {

    @Autowired
    private CrmNationalValidator crmNationalValidator;

    @Override
    public void initialize(CRM constraintAnnotation) {}

    @Override
    public boolean isValid(String crm, ConstraintValidatorContext context) {
        return crmNationalValidator.isCrmValid(crm);
    }
}