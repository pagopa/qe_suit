package it.frontend.e2e.framework.annotation.processor.xpath.util;

import it.frontend.e2e.framework.annotation.processor.xpath.model.XPathData;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class XPathAnnotationReader {

    private final XPathAnnotationHelper annotationHelper;

    public XPathAnnotationReader(XPathAnnotationHelper annotationHelper) {
        this.annotationHelper = annotationHelper;
    }

    public XPathData readTypeXPath(TypeElement type) {
        return readXPathFrom(type);
    }

    public XPathData readMethodXPath(ExecutableElement method) {
        return readXPathFrom(method);
    }

    private XPathData readXPathFrom(Element element) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (!annotationHelper.isXPathAnnotation(mirror)) {
                continue;
            }

            AnnotationValue valueNode = annotationHelper.getValueNode(mirror);
            String xpath = annotationHelper.extractXPathValue(valueNode);
            return new XPathData(mirror, valueNode, xpath);
        }

        return XPathData.absent();
    }
}

