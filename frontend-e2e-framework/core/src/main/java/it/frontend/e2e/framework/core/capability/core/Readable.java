package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

import java.util.List;

public interface Readable<T, S extends Selector, L extends Location, E extends AbstractPresentationElement<S, L>> extends Capability {
    // Lettura singolo elemento
    T read();
    T readAndAssert(T expected);
    T readAndAssert(AssertionAction<T> assertionAction);

    // Lettura lista di elementi
    List<T> readAll();
    List<T> readAllAndAssert(List<T> expected);
    List<T> readAllAndAssert(AssertionAction<List<T>> assertionAction);
}
