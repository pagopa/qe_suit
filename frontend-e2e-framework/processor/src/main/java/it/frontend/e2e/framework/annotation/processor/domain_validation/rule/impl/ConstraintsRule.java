package it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl;

import it.frontend.e2e.framework.annotation.processor.domain_validation.result.ValidationResult;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.DomainRule;
import it.frontend.e2e.framework.core.meta.Descriptor;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public final class ConstraintsRule implements DomainRule {

    @Override
    public void apply(Descriptor d, Messager messager, ValidationResult result) {
        if (d.constraints == null || d.constraints.isEmpty()) {
            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "No constraints defined for domain " + d.domainId
            );
        }
    }
}
