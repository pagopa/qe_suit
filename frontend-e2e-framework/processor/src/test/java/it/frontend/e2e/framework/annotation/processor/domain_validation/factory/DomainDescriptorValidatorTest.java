package it.frontend.e2e.framework.annotation.processor.domain_validation.factory;

import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.ConstraintsRule;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.DomainIdRule;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.ScopesRule;
import it.frontend.e2e.framework.annotation.processor.domain_validation.validator.DomainValidator;
import it.frontend.e2e.framework.core.meta.Descriptor;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Messager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DomainDescriptorValidatorTest {

    @Test
    void shouldThrowExceptionWhenAnyRuleReportsError() {
        Descriptor d = new Descriptor();
        d.domainId = null;
        d.schemaVersion = 1;

        DomainValidator validator =
                new DomainValidator(List.of(
                        new DomainIdRule()
                ));

        Messager messager = mock(Messager.class);

        assertThrows(IllegalStateException.class,
                () -> validator.validate(d, messager));
    }

    @Test
    void shouldPassWhenOnlyWarningsOrNotesOccur() {
        Descriptor d = new Descriptor();
        d.domainId = "test";
        d.schemaVersion = 1;
        d.scopes = null; // warning
        d.constraints = null; // note

        DomainValidator validator =
                new DomainValidator(List.of(
                        new ScopesRule(),
                        new ConstraintsRule()
                ));

        Messager messager = mock(Messager.class);

        // NON deve lanciare
        validator.validate(d, messager);
    }
}
