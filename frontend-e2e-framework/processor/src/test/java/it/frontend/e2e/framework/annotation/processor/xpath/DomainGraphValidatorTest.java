package it.frontend.e2e.framework.annotation.processor.xpath;

import it.frontend.e2e.framework.annotation.processor.xpath.exception.XPathValidationException;
import it.frontend.e2e.framework.annotation.processor.xpath.graph.DomainGraphValidator;
import it.frontend.e2e.framework.annotation.processor.xpath.graph.TypeHierarchyInspector;
import it.frontend.e2e.framework.annotation.processor.xpath.model.ValidationFailure;
import it.frontend.e2e.framework.annotation.processor.xpath.rule.XPathRuleEvaluator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DomainGraphValidatorTest {

    @Test
    @DisplayName("Restituisce false quando il tipo iniziale è null e valorizza il motivo del fallimento")
    void shouldReturnFalseWhenTypeIsNullAndExposeFailureReason() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        assertFalse(validator.validate(null, new HashSet<>()));

        ValidationFailure failure = validator.lastFailure().orElseThrow();
        assertNull(failure.type());
        assertNull(failure.method());
        assertEquals("Current type is null", failure.reason());
        verifyNoInteractions(inspector, evaluator);
    }

    @Test
    @DisplayName("Restituisce false quando il tipo è una Capability foglia")
    void shouldReturnFalseWhenTypeIsSimpleCapability() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);
        TypeElement type = mock(TypeElement.class);

        when(inspector.isSimpleCapability(type)).thenReturn(true);

        assertFalse(validator.validate(type, new HashSet<>()));

        ValidationFailure failure = validator.lastFailure().orElseThrow();
        assertEquals(type, failure.type());
        assertEquals("Type is a Capability leaf and cannot be validated as DomainElement", failure.reason());
    }

    @Test
    @DisplayName("Restituisce false quando il tipo non estende DomainElement")
    void shouldReturnFalseWhenTypeIsNotDomainElement() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);
        TypeElement type = mock(TypeElement.class);

        when(inspector.isSimpleCapability(type)).thenReturn(false);
        when(inspector.isDomainElement(type)).thenReturn(false);

        assertFalse(validator.validate(type, new HashSet<>()));

        ValidationFailure failure = validator.lastFailure().orElseThrow();
        assertEquals(type, failure.type());
        assertEquals("Type does not extend DomainElement", failure.reason());
    }

    @Test
    @DisplayName("Restituisce false quando rileva un ciclo nel grafo dei DomainElement")
    void shouldReturnFalseWhenCycleDetected() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        TypeElement type = mock(TypeElement.class);
        Set<TypeElement> visiting = new HashSet<>();
        visiting.add(type);

        when(inspector.isSimpleCapability(type)).thenReturn(false);
        when(inspector.isDomainElement(type)).thenReturn(true);

        assertFalse(validator.validate(type, visiting));

        ValidationFailure failure = validator.lastFailure().orElseThrow();
        assertEquals(type, failure.type());
        assertEquals("Cycle detected while traversing DomainElement graph", failure.reason());
    }

    @Test
    @DisplayName("Restituisce true quando tutti i metodi figli hanno uno XPath valido")
    void shouldReturnTrueWhenAllMethodsHaveValidXPath() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        TypeElement type = mock(TypeElement.class);
        ExecutableElement method = mock(ExecutableElement.class);
        Set<TypeElement> visiting = new HashSet<>();

        when(inspector.isSimpleCapability(type)).thenReturn(false);
        when(inspector.isDomainElement(type)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(type)).thenReturn(true);
        when(inspector.childMethodsOf(type)).thenReturn(List.of(method));
        when(evaluator.hasValidMethodAnnotation(method, true)).thenReturn(true);

        assertTrue(validator.validate(type, visiting));
        assertTrue(validator.lastFailure().isEmpty());
        assertFalse(visiting.contains(type));
    }

    @Test
    @DisplayName("Lancia XPathValidationException quando fallisce la validazione dell'XPath dell'interfaccia")
    void shouldThrowXPathValidationExceptionWhenTypeXPathValidationFails() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);
        TypeElement type = mock(TypeElement.class);

        when(inspector.isSimpleCapability(type)).thenReturn(false);
        when(inspector.isDomainElement(type)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(type)).thenThrow(new IllegalArgumentException("bad type xpath"));
        when(evaluator.readTypeXPath(type)).thenReturn("-//*[@id='x']");

        XPathValidationException ex = assertThrows(XPathValidationException.class,
                () -> validator.validate(type, new HashSet<>()));

        assertEquals(type, ex.getType());
        assertNull(ex.getMethod());
        assertEquals("-//*[@id='x']", ex.getXPathValue());
        assertEquals("bad type xpath", ex.getMessage());
    }

    @Test
    @DisplayName("Lancia XPathValidationException quando fallisce la validazione dell'XPath del metodo")
    void shouldThrowXPathValidationExceptionWhenMethodXPathValidationFails() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        TypeElement type = mock(TypeElement.class);
        ExecutableElement method = mock(ExecutableElement.class);

        when(inspector.isSimpleCapability(type)).thenReturn(false);
        when(inspector.isDomainElement(type)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(type)).thenReturn(true);
        when(inspector.childMethodsOf(type)).thenReturn(List.of(method));
        when(evaluator.hasValidMethodAnnotation(method, true)).thenThrow(new IllegalArgumentException("bad method xpath"));
        when(evaluator.readMethodXPath(method)).thenReturn("mlmlml//*[@id='x']");

        XPathValidationException ex = assertThrows(XPathValidationException.class,
                () -> validator.validate(type, new HashSet<>()));

        assertEquals(type, ex.getType());
        assertEquals(method, ex.getMethod());
        assertEquals("mlmlml//*[@id='x']", ex.getXPathValue());
        assertEquals("bad method xpath", ex.getMessage());
    }

    @Test
    @DisplayName("Restituisce false quando un metodo senza XPath non ritorna un tipo dichiarato")
    void shouldReturnFalseWhenMethodHasNoXPathAndReturnTypeIsNull() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        TypeElement type = mock(TypeElement.class);
        ExecutableElement method = mock(ExecutableElement.class);

        when(inspector.isSimpleCapability(type)).thenReturn(false);
        when(inspector.isDomainElement(type)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(type)).thenReturn(false);
        when(inspector.childMethodsOf(type)).thenReturn(List.of(method));
        when(evaluator.hasValidMethodAnnotation(method, false)).thenReturn(false);
        when(inspector.getReturnedTypeElement(method)).thenReturn(null);

        assertFalse(validator.validate(type, new HashSet<>()));

        ValidationFailure failure = validator.lastFailure().orElseThrow();
        assertEquals(type, failure.type());
        assertEquals(method, failure.method());
        assertEquals("Method has no @XPath and does not return a declared type", failure.reason());
    }

    @Test
    @DisplayName("Restituisce false quando un metodo senza XPath ritorna una Capability foglia")
    void shouldReturnFalseWhenMethodHasNoXPathAndReturnsSimpleCapability() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        TypeElement type = mock(TypeElement.class);
        TypeElement returnType = mock(TypeElement.class);
        ExecutableElement method = mock(ExecutableElement.class);

        when(inspector.isSimpleCapability(type)).thenReturn(false);
        when(inspector.isDomainElement(type)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(type)).thenReturn(false);
        when(inspector.childMethodsOf(type)).thenReturn(List.of(method));
        when(evaluator.hasValidMethodAnnotation(method, false)).thenReturn(false);
        when(inspector.getReturnedTypeElement(method)).thenReturn(returnType);
        when(inspector.isSimpleCapability(returnType)).thenReturn(true);

        assertFalse(validator.validate(type, new HashSet<>()));

        ValidationFailure failure = validator.lastFailure().orElseThrow();
        assertEquals(type, failure.type());
        assertEquals(method, failure.method());
        assertEquals("Method has no @XPath and returns a Capability leaf", failure.reason());
    }

    @Test
    @DisplayName("Restituisce false quando un metodo senza XPath ritorna un tipo che non e DomainElement")
    void shouldReturnFalseWhenMethodHasNoXPathAndReturnTypeIsNotDomainElement() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        TypeElement rootType = mock(TypeElement.class);
        TypeElement returnType = mock(TypeElement.class);
        ExecutableElement method = mock(ExecutableElement.class);

        when(inspector.isSimpleCapability(rootType)).thenReturn(false);
        when(inspector.isDomainElement(rootType)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(rootType)).thenReturn(true);
        when(inspector.childMethodsOf(rootType)).thenReturn(List.of(method));
        when(evaluator.hasValidMethodAnnotation(method, true)).thenReturn(false);
        when(inspector.getReturnedTypeElement(method)).thenReturn(returnType);
        when(inspector.isSimpleCapability(returnType)).thenReturn(false);
        when(inspector.isDomainElement(returnType)).thenReturn(false);

        assertFalse(validator.validate(rootType, new HashSet<>()));

        ValidationFailure failure = validator.lastFailure().orElseThrow();
        assertEquals(rootType, failure.type());
        assertEquals(method, failure.method());
        assertEquals("Method has no @XPath and return type is not a DomainElement", failure.reason());
    }

    @Test
    @DisplayName("Comportamento attuale: restituisce true anche se si arriva a un nodo foglia DomainElement senza alcun XPath valido")
    void shouldReturnTrueWhenInvalidMethodLeadsToValidNestedDomainGraph() {
        TypeHierarchyInspector inspector = mock(TypeHierarchyInspector.class);
        XPathRuleEvaluator evaluator = mock(XPathRuleEvaluator.class);
        DomainGraphValidator validator = new DomainGraphValidator(inspector, evaluator);

        TypeElement rootType = mock(TypeElement.class);
        TypeElement nestedType = mock(TypeElement.class);
        ExecutableElement rootMethod = mock(ExecutableElement.class);

        when(inspector.isSimpleCapability(rootType)).thenReturn(false);
        when(inspector.isDomainElement(rootType)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(rootType)).thenReturn(true);
        when(inspector.childMethodsOf(rootType)).thenReturn(List.of(rootMethod));
        when(evaluator.hasValidMethodAnnotation(rootMethod, true)).thenReturn(false);
        when(inspector.getReturnedTypeElement(rootMethod)).thenReturn(nestedType);

        when(inspector.isSimpleCapability(nestedType)).thenReturn(false);
        when(inspector.isDomainElement(nestedType)).thenReturn(true);
        when(evaluator.hasValidTypeAnnotation(nestedType)).thenReturn(false);
        when(inspector.childMethodsOf(nestedType)).thenReturn(List.of());

        assertTrue(validator.validate(rootType, new HashSet<>()));
        assertTrue(validator.lastFailure().isEmpty());
    }
}

