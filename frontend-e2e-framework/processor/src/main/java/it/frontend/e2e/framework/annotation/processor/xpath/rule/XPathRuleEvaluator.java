package it.frontend.e2e.framework.annotation.processor.xpath.rule;

import it.frontend.e2e.framework.annotation.processor.xpath.model.XPathData;
import it.frontend.e2e.framework.annotation.processor.xpath.util.XPathAnnotationReader;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class XPathRuleEvaluator {

    private final XPathValidator validator;
    private final XPathAnnotationReader annotationReader;

    public XPathRuleEvaluator(XPathValidator validator, XPathAnnotationReader annotationReader) {
        this.validator = validator;
        this.annotationReader = annotationReader;
    }

    public boolean hasValidTypeAnnotation(TypeElement type) {
        XPathData xpathData = annotationReader.readTypeXPath(type);
        if (!xpathData.present()) {
            return false;
        }
        return validator.isValidTypeXPath(xpathData.value());
    }

    public boolean hasValidMethodAnnotation(ExecutableElement method, boolean hasTypeAnnotation) {
        XPathData xpathData = annotationReader.readMethodXPath(method);
        if (!xpathData.present()) {
            return false;
        }
        return hasTypeAnnotation
                ? validator.isValidMethodXPath(xpathData.value())
                : validator.isValidTypeXPath(xpathData.value());
    }

    public String readTypeXPath(TypeElement type) {
        XPathData xpathData = annotationReader.readTypeXPath(type);
        return xpathData.present() ? xpathData.value() : null;
    }

    public String readMethodXPath(ExecutableElement method) {
        XPathData xpathData = annotationReader.readMethodXPath(method);
        return xpathData.present() ? xpathData.value() : null;
    }
}
