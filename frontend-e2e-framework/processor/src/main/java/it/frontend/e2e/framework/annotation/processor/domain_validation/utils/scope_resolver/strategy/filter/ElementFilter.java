package it.frontend.e2e.framework.annotation.processor.domain_validation.utils.scope_resolver.strategy.filter;

import javax.lang.model.element.TypeElement;

public interface ElementFilter {
    boolean accepts(TypeElement element);
}

