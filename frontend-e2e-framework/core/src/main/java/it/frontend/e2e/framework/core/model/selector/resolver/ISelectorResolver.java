package it.frontend.e2e.framework.core.model.selector.resolver;

import it.frontend.e2e.framework.core.model.selector.Selector;

@FunctionalInterface
public interface ISelectorResolver<S extends Selector> {
    S resolve(String xpath);
}
