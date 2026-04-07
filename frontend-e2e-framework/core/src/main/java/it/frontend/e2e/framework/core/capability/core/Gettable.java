package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

import java.util.Optional;

public interface Gettable<S extends Selector, L extends Location, E extends AbstractPresentationElement<S,L>> extends Capability {
    Optional<E> get();
}
