package it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.filter;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public final class AcceptsKindFilter implements ElementFilter {

    private final Set<ElementKind> acceptedKinds;

    public AcceptsKindFilter(Set<ElementKind> acceptedKinds) {
        this.acceptedKinds = acceptedKinds == null || acceptedKinds.isEmpty()
                ? Set.of(ElementKind.INTERFACE) // default
                : acceptedKinds;
    }

    public static AcceptsKindFilter of(Set<ElementKind> acceptedKinds){
        return new AcceptsKindFilter(acceptedKinds);
    }

    @Override
    public boolean accepts(TypeElement element) {
        return acceptedKinds.contains(element.getKind());
    }
}

