package it.frontend.e2e.framework.annotation.processor.xpath.model;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;

public record XPathData(AnnotationMirror mirror, AnnotationValue valueNode, String value) {
    public static XPathData absent() {
        return new XPathData(null, null, null);
    }

    public boolean present() {
        return mirror != null && value != null && !value.trim().isEmpty();
    }
}

