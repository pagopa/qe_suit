package it.frontend.e2e.framework.core;

import it.frontend.e2e.framework.core.adapter.IPresentationApiAdapter;
import it.frontend.e2e.framework.core.binder.IBinder;
import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;

public interface IPresentationGateway<S extends Selector, L extends Location, E extends AbstractPresentationElement<S,L>>
        extends IPresentationApiAdapter<S, L, E>, IBinder {
}
