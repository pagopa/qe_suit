package it.frontend.e2e.framework.annotation.processor.domain_validation.rule;

import it.frontend.e2e.framework.annotation.processor.domain_validation.result.ValidationResult;
import it.frontend.e2e.framework.annotation.processor.domain_validation.rule.impl.DomainIdRule;
import it.frontend.e2e.framework.core.meta.Descriptor;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;


class DomainIdRuleTest {

    private final DomainIdRule rule = new DomainIdRule();

    @Test
    void shouldReportErrorWhenDomainIdIsNull() {
        Descriptor d = new Descriptor();
        d.domainId = null;

        Messager messager = mock(Messager.class);
        ValidationResult result = new ValidationResult();

        rule.apply(d, messager, result);

        verify(messager).printMessage(
                eq(Diagnostic.Kind.ERROR),
                contains("domainId is missing")
        );
        assert(result.hasErrors());
    }

    @Test
    void shouldReportErrorWhenDomainIdIsBlank() {
        Descriptor d = new Descriptor();
        d.domainId = "   ";

        Messager messager = mock(Messager.class);
        ValidationResult result = new ValidationResult();

        rule.apply(d, messager, result);

        verify(messager).printMessage(
                eq(Diagnostic.Kind.ERROR),
                anyString()
        );
        assert(result.hasErrors());
    }

    @Test
    void shouldPassWhenDomainIdIsValid() {
        Descriptor d = new Descriptor();
        d.domainId = "payments";

        Messager messager = mock(Messager.class);
        ValidationResult result = new ValidationResult();

        rule.apply(d, messager, result);

        verifyNoInteractions(messager);
        assert(!result.hasErrors());
    }
}
