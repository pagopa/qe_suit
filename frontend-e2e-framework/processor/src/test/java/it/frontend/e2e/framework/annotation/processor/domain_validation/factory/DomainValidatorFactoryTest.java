package it.frontend.e2e.framework.annotation.processor.domain_validation.factory;

import it.frontend.e2e.framework.annotation.processor.domain_validation.validator.IDomainValidator;
import it.frontend.e2e.framework.core.meta.Descriptor;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DomainValidatorFactoryTest {

    @Test
    void shouldReturnValidatorForSupportedSchema() {
        IDomainValidatorFactory factory =
                new DomainValidatorFactory();

        IDomainValidator validator = factory.forSchema(1);

        assertNotNull(validator);
    }

    @Test
    void shouldReportErrorForUnsupportedSchemaVersion() {
        IDomainValidatorFactory factory =
                new DomainValidatorFactory();

        Descriptor d = new Descriptor();
        d.schemaVersion = 99;
        d.domainId = "test";

        Messager messager = mock(Messager.class);

        IDomainValidator validator = factory.forSchema(99);

        // NON deve lanciare eccezione
        validator.validate(d, messager);

        // Deve segnalare ERROR
        verify(messager).printMessage(
                eq(Diagnostic.Kind.ERROR),
                contains("Unsupported schemaVersion")
        );
    }

}

