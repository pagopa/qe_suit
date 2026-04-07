package it.frontend.e2e.framework.annotation.processor.xpath.filter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.util.List;

/**
 * Filtro per identificare elementi che estendono DomainElement.
 * Responsabile della selezione degli elementi da processare.
 */
public class DomainElementFilter {
    private static final String DOMAIN_ELEMENT_CLASS = "it.frontend.e2e.framework.core.model.DomainElement";

    private final ProcessingEnvironment processingEnv;

    public DomainElementFilter(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    /**
     * Filtra gli elementi del round di elaborazione per ottenere solo quelli
     * che estendono DomainElement (escludendo DomainElement stesso).
     *
     * @param roundEnv l'ambiente del round di elaborazione
     * @return lista di TypeElement che estendono DomainElement
     */
    public List<TypeElement> filterDomainElements(RoundEnvironment roundEnv) {
        TypeElement domainElementType = processingEnv.getElementUtils().getTypeElement(DOMAIN_ELEMENT_CLASS);
        if (domainElementType == null) {
            return List.of();
        }

        Types types = processingEnv.getTypeUtils();

        return roundEnv.getRootElements().stream()
                .filter(e -> e instanceof TypeElement)
                .map(e -> (TypeElement) e)
                .filter(te -> isSubtypeOfDomainElement(te, domainElementType, types))
                .filter(te -> !isSameAsDomainElement(te, domainElementType, types))
                .toList();
    }

    private boolean isSubtypeOfDomainElement(TypeElement type, TypeElement domainElement, Types types) {
        return types.isAssignable(types.erasure(type.asType()), types.erasure(domainElement.asType()));
    }

    private boolean isSameAsDomainElement(TypeElement type, TypeElement domainElement, Types types) {
        return types.isSameType(types.erasure(type.asType()), types.erasure(domainElement.asType()));
    }
}

