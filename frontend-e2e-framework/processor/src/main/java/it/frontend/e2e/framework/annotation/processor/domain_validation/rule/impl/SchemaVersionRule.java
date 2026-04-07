package it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl;

import it.frontend.e2e.framework.annotation.processor.domain_validation.result.ValidationResult;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.DomainRule;
import it.frontend.e2e.framework.core.meta.Descriptor;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public final class SchemaVersionRule implements DomainRule {

    private final int supportedVersion;

    public SchemaVersionRule(int supportedVersion) {
        this.supportedVersion = supportedVersion;
    }

    @Override
    public void apply(Descriptor d, Messager messager, ValidationResult result) {
        if (d.schemaVersion != supportedVersion) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Unsupported schemaVersion " + d.schemaVersion +
                            " in domain " + d.domainId
            );
            result.error();
        }
    }
}
