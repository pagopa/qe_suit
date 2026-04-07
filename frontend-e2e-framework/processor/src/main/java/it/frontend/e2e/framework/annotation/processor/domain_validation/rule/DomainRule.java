package it.frontend.e2e.framework.annotation.processor.domain_validation.rule;

import it.frontend.e2e.framework.annotation.processor.domain_validation.result.ValidationResult;
import it.frontend.e2e.framework.core.meta.Descriptor;

import javax.annotation.processing.Messager;

public interface DomainRule {
    void apply(Descriptor descriptor, Messager messager, ValidationResult result);
}
