package it.frontend.e2e.framework.annotation.processor.domain_validation.validator;


import it.frontend.e2e.framework.annotation.processor.domain_validation.result.ValidationResult;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.DomainRule;
import it.frontend.e2e.framework.core.meta.Descriptor;

import javax.annotation.processing.Messager;
import java.util.List;

public final class DomainValidator implements IDomainValidator {

    private final List<DomainRule> rules;

    public DomainValidator(List<DomainRule> rules) {
        this.rules = List.copyOf(rules);
    }

    @Override
    public void validate(Descriptor descriptor, Messager messager) {
        ValidationResult result = new ValidationResult();
        for (DomainRule rule : rules) {
            rule.apply(descriptor, messager, result);
        }
        if (result.hasErrors()) {
            throw new IllegalStateException("Descriptor validation failed");
        }
    }

}
