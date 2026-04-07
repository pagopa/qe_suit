package it.frontend.e2e.framework.annotation.processor.domain_validation.factory;

import it.frontend.e2e.framework.annotation.processor.domain_validation.validator.IDomainValidator;

public interface IDomainValidatorFactory {
    IDomainValidator forSchema(int schemaVersion);
}
