package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

public interface Readable<T, S extends Selector, L extends Location, E extends AbstractPresentationElement<S,L>> extends Capability {
    T read();
    T readAndAssert();
    T readAndAssert(AssertionAction<E> assertionAction);
}
