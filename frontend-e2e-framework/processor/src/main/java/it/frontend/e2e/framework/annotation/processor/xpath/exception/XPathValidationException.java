package it.frontend.e2e.framework.annotation.processor.xpath.exception;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class XPathValidationException extends RuntimeException {
    private final TypeElement type;
    private final ExecutableElement method;
    private final String xpathValue;

    public XPathValidationException(TypeElement type, ExecutableElement method, String xpathValue, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
        this.method = method;
        this.xpathValue = xpathValue;
    }

    public TypeElement getType() {
        return type;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public String getXPathValue() {
        return xpathValue;
    }
}

