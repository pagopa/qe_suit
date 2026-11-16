package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.DomainElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

import java.util.List;
import java.util.Optional;

public interface Gettable<S extends Selector, L extends Location, E extends AbstractPresentationElement<S,L> > extends Capability {
    Optional<E> get();

    default boolean exists() {
        return exists(FindPolicy.VISIBLE);
    }

    boolean exists(FindPolicy policy);

    Optional<E> get(FindPolicy policy);
    Optional<List<E>> getAll();

    Optional<List<E>> getAll(FindPolicy policy);

    enum FindPolicy {
        CLICKABLE,
        VISIBLE,
        PRESENT
    }
}
