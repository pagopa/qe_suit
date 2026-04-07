package it.frontend.e2e.framework.annotation.processor.xpath.util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;

/**
 * Helper per l'estrazione di informazioni dalle annotazioni @XPath.
 * Responsabile dell'identificazione e dell'estrazione dei valori dalle annotazioni.
 */
public class XPathAnnotationHelper {
    private static final String XPATH_ANNOTATION_SIMPLE_NAME = "XPath";
    private static final String XPATH_VALUE_MEMBER = "value";

    /**
     * Verifica se un'annotazione è di tipo @XPath.
     *
     * @param mirror l'annotazione da verificare
     * @return true se è un'annotazione @XPath, false altrimenti
     */
    public boolean isXPathAnnotation(AnnotationMirror mirror) {
        String simpleName = mirror.getAnnotationType().asElement().getSimpleName().toString();
        return XPATH_ANNOTATION_SIMPLE_NAME.equals(simpleName);
    }

    /**
     * Estrae il nodo del valore dall'annotazione @XPath.
     *
     * @param mirror l'annotazione da cui estrarre il valore
     * @return il nodo del valore o null se non trovato
     */
    public AnnotationValue getValueNode(AnnotationMirror mirror) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
            if (XPATH_VALUE_MEMBER.equals(entry.getKey().getSimpleName().toString())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Estrae il valore XPath dal nodo dell'annotazione.
     *
     * @param valueNode il nodo contenente il valore
     * @return il valore XPath come stringa o null se il nodo è null
     */
    public String extractXPathValue(AnnotationValue valueNode) {
        return valueNode != null ? String.valueOf(valueNode.getValue()) : null;
    }
}

