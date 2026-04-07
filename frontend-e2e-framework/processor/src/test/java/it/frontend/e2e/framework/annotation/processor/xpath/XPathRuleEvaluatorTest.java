package it.frontend.e2e.framework.annotation.processor.xpath;

import it.frontend.e2e.framework.annotation.processor.xpath.model.XPathData;
import it.frontend.e2e.framework.annotation.processor.xpath.rule.XPathRuleEvaluator;
import it.frontend.e2e.framework.annotation.processor.xpath.rule.XPathValidator;
import it.frontend.e2e.framework.annotation.processor.xpath.util.XPathAnnotationReader;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class XPathRuleEvaluatorTest {

    @Test
    void shouldReturnFalseWhenTypeXPathIsAbsent() {
        XPathValidator validator = mock(XPathValidator.class);
        XPathAnnotationReader reader = mock(XPathAnnotationReader.class);
        TypeElement type = mock(TypeElement.class);

        when(reader.readTypeXPath(type)).thenReturn(XPathData.absent());

        XPathRuleEvaluator evaluator = new XPathRuleEvaluator(validator, reader);

        assertFalse(evaluator.hasValidTypeAnnotation(type));
        verifyNoInteractions(validator);
    }

    @Test
    void shouldValidateTypeXPathWhenPresent() {
        XPathValidator validator = mock(XPathValidator.class);
        XPathAnnotationReader reader = mock(XPathAnnotationReader.class);
        TypeElement type = mock(TypeElement.class);

        XPathData xpathData = new XPathData(mock(AnnotationMirror.class), mock(AnnotationValue.class), "//*[@id='username']");
        when(reader.readTypeXPath(type)).thenReturn(xpathData);
        when(validator.isValidTypeXPath(xpathData.value())).thenReturn(true);

        XPathRuleEvaluator evaluator = new XPathRuleEvaluator(validator, reader);

        assertTrue(evaluator.hasValidTypeAnnotation(type));
        verify(validator).isValidTypeXPath(xpathData.value());
    }

    @Test
    void shouldReturnFalseWhenMethodXPathIsAbsent() {
        XPathValidator validator = mock(XPathValidator.class);
        XPathAnnotationReader reader = mock(XPathAnnotationReader.class);
        ExecutableElement method = mock(ExecutableElement.class);

        when(reader.readMethodXPath(method)).thenReturn(XPathData.absent());

        XPathRuleEvaluator evaluator = new XPathRuleEvaluator(validator, reader);

        assertFalse(evaluator.hasValidMethodAnnotation(method, true));
        verifyNoInteractions(validator);
    }

    @Test
    void shouldValidateMethodXPathWhenTypeHasXPath() {
        XPathValidator validator = mock(XPathValidator.class);
        XPathAnnotationReader reader = mock(XPathAnnotationReader.class);
        ExecutableElement method = mock(ExecutableElement.class);

        XPathData xpathData = new XPathData(mock(AnnotationMirror.class), mock(AnnotationValue.class), ".//*[@id='username']");
        when(reader.readMethodXPath(method)).thenReturn(xpathData);
        when(validator.isValidMethodXPath(xpathData.value())).thenReturn(true);

        XPathRuleEvaluator evaluator = new XPathRuleEvaluator(validator, reader);

        assertTrue(evaluator.hasValidMethodAnnotation(method, true));
        verify(validator).isValidMethodXPath(xpathData.value());
        verify(validator, never()).isValidTypeXPath(anyString());
    }

    @Test
    void shouldValidateTypeXPathWhenTypeHasNoXPath() {
        XPathValidator validator = mock(XPathValidator.class);
        XPathAnnotationReader reader = mock(XPathAnnotationReader.class);
        ExecutableElement method = mock(ExecutableElement.class);

        XPathData xpathData = new XPathData(mock(AnnotationMirror.class), mock(AnnotationValue.class), "//*[@id='username']");
        when(reader.readMethodXPath(method)).thenReturn(xpathData);
        when(validator.isValidTypeXPath(xpathData.value())).thenReturn(true);

        XPathRuleEvaluator evaluator = new XPathRuleEvaluator(validator, reader);

        assertTrue(evaluator.hasValidMethodAnnotation(method, false));
        verify(validator).isValidTypeXPath(xpathData.value());
        verify(validator, never()).isValidMethodXPath(anyString());
    }
}

