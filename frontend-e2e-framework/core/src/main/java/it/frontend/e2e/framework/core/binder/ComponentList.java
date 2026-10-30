package it.frontend.e2e.framework.core.binder;

import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.capability.core.Gettable;

import java.util.List;

public interface ComponentList<T extends Capability> extends List<T> {
    int size(Gettable.FindPolicy findPolicy);

    T get(int index, Gettable.FindPolicy findPolicy);
}
