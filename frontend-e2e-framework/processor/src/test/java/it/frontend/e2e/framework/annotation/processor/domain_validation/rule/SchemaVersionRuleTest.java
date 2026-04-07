package it.frontend.e2e.framework.annotation.processor.domain_validation.rule;

import it.frontend.e2e.framework.annotation.processor.domain_validation.result.ValidationResult;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.SchemaVersionRule;
import it.frontend.e2e.framework.core.meta.Descriptor;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SchemaVersionRuleTest {

    @Test
    void shouldFailWhenSchemaVersionIsUnsupported() {
        Descriptor d = new Descriptor();
        d.schemaVersion = 2;
        d.domainId = "test";

        SchemaVersionRule rule = new SchemaVersionRule(1);
        Messager messager = mock(Messager.class);
        ValidationResult result = new ValidationResult();

        rule.apply(d, messager, result);

        verify(messager).printMessage(
                eq(Diagnostic.Kind.ERROR),
                contains("Unsupported schemaVersion")
        );
        assert(result.hasErrors());
    }

    @Test
    void shouldPassWhenSchemaVersionMatches() {
        Descriptor d = new Descriptor();
        d.schemaVersion = 1;

        SchemaVersionRule rule = new SchemaVersionRule(1);
        Messager messager = mock(Messager.class);
        ValidationResult result = new ValidationResult();

        rule.apply(d, messager, result);

        verifyNoInteractions(messager);
        assert(!result.hasErrors());
    }
}
