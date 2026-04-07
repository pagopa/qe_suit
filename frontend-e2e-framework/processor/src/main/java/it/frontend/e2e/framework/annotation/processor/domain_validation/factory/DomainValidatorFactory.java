package it.frontend.e2e.framework.annotation.processor.domain_validation.factory;

import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.ConstraintsRule;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.DomainIdRule;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.SchemaVersionRule;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.ScopesRule;
import it.frontend.e2e.framework.annotation.processor.domain_validation.validator.DomainValidator;
import it.frontend.e2e.framework.annotation.processor.domain_validation.validator.IDomainValidator;

import javax.tools.Diagnostic;
import java.util.List;

public final class DomainValidatorFactory implements IDomainValidatorFactory {

    @Override
    public IDomainValidator forSchema(int schemaVersion) {
        return switch (schemaVersion) {
            case 1 -> v1();
            default -> unsupported(schemaVersion);
        };
    }

    private IDomainValidator v1() {
        return new DomainValidator(List.of(
                new SchemaVersionRule(1),
                new DomainIdRule(),
                new ScopesRule(),
                new ConstraintsRule()
        ));
    }

    private IDomainValidator unsupported(int schemaVersion) {
        return (d, messager) -> messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Unsupported schemaVersion " + schemaVersion +
                        " in domain " + d.domainId
        );
    }
}
