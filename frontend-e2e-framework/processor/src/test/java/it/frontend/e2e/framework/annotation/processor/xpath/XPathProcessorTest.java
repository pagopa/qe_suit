package it.frontend.e2e.framework.annotation.processor.xpath;

import it.frontend.e2e.framework.annotation.processor.xpath.filter.DomainElementFilter;
import it.frontend.e2e.framework.annotation.processor.xpath.graph.DomainGraphValidator;
import org.junit.jupiter.api.Test;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class XPathProcessorTest {

    @Test
    void shouldReturnFalseAndEmitNoErrorsWhenAllDomainElementsAreValid() throws Exception {
        XPathProcessor processor = new XPathProcessor();
        ProcessingEnvironment env = mock(ProcessingEnvironment.class);
        Messager messager = mock(Messager.class);
        RoundEnvironment roundEnv = mock(RoundEnvironment.class);
        DomainElementFilter filter = mock(DomainElementFilter.class);
        DomainGraphValidator graphValidator = mock(DomainGraphValidator.class);
        TypeElement type = mock(TypeElement.class);

        when(env.getMessager()).thenReturn(messager);
        processor.init(env);

        setField(processor, "domainElementFilter", filter);
        setField(processor, "domainGraphValidator", graphValidator);

        when(filter.filterDomainElements(roundEnv)).thenReturn(List.of(type));
        when(graphValidator.validate(eq(type), anySet())).thenReturn(true);

        boolean result = processor.process(Set.of(), roundEnv);

        assertFalse(result);
        verify(filter).filterDomainElements(roundEnv);
        verify(graphValidator).validate(eq(type), anySet());
        verify(messager, never()).printMessage(eq(Diagnostic.Kind.ERROR), anyString(), any(TypeElement.class));
    }

    @Test
    void shouldEmitErrorWhenDomainElementGraphIsInvalid() throws Exception {
        XPathProcessor processor = new XPathProcessor();
        ProcessingEnvironment env = mock(ProcessingEnvironment.class);
        Messager messager = mock(Messager.class);
        RoundEnvironment roundEnv = mock(RoundEnvironment.class);
        DomainElementFilter filter = mock(DomainElementFilter.class);
        DomainGraphValidator graphValidator = mock(DomainGraphValidator.class);
        TypeElement invalidType = mock(TypeElement.class);

        when(env.getMessager()).thenReturn(messager);
        processor.init(env);

        setField(processor, "domainElementFilter", filter);
        setField(processor, "domainGraphValidator", graphValidator);

        when(filter.filterDomainElements(roundEnv)).thenReturn(List.of(invalidType));
        when(graphValidator.validate(eq(invalidType), anySet())).thenReturn(false);

        boolean result = processor.process(Set.of(), roundEnv);

        assertFalse(result);
        verify(messager).printMessage(
                eq(Diagnostic.Kind.ERROR),
                contains("Invalid DomainElement graph"),
                eq(invalidType)
        );
    }

    @Test
    void shouldSkipValidationWhenNoDomainElementsAreFound() throws Exception {
        XPathProcessor processor = new XPathProcessor();
        ProcessingEnvironment env = mock(ProcessingEnvironment.class);
        Messager messager = mock(Messager.class);
        RoundEnvironment roundEnv = mock(RoundEnvironment.class);
        DomainElementFilter filter = mock(DomainElementFilter.class);
        DomainGraphValidator graphValidator = mock(DomainGraphValidator.class);

        when(env.getMessager()).thenReturn(messager);
        processor.init(env);

        setField(processor, "domainElementFilter", filter);
        setField(processor, "domainGraphValidator", graphValidator);

        when(filter.filterDomainElements(roundEnv)).thenReturn(List.of());

        boolean result = processor.process(Set.of(), roundEnv);

        assertFalse(result);
        verifyNoInteractions(graphValidator);
        verify(messager, never()).printMessage(eq(Diagnostic.Kind.ERROR), anyString(), any(TypeElement.class));
    }

    @Test
    void shouldEmitCompactXPathErrorWhenValidationThrowsIllegalArgumentException() throws Exception {
        XPathProcessor processor = new XPathProcessor();
        ProcessingEnvironment env = mock(ProcessingEnvironment.class);
        Messager messager = mock(Messager.class);
        RoundEnvironment roundEnv = mock(RoundEnvironment.class);
        DomainElementFilter filter = mock(DomainElementFilter.class);
        DomainGraphValidator graphValidator = mock(DomainGraphValidator.class);
        TypeElement type = mock(TypeElement.class);

        when(env.getMessager()).thenReturn(messager);
        processor.init(env);

        setField(processor, "domainElementFilter", filter);
        setField(processor, "domainGraphValidator", graphValidator);

        when(filter.filterDomainElements(roundEnv)).thenReturn(List.of(type));
        when(graphValidator.validate(eq(type), anySet()))
                .thenThrow(new IllegalArgumentException("XPath must start with '/', './', './/' or '(...)'"));

        boolean result = processor.process(Set.of(), roundEnv);

        assertFalse(result);
        verify(messager).printMessage(
                eq(Diagnostic.Kind.ERROR),
                contains("Invalid @XPath: XPath must start with '/', './', './/' or '(...)'"),
                eq(type)
        );
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
