package it.frontend.e2e.framework.core.model;

import it.frontend.e2e.framework.core.model.location.Location;
import it.frontend.e2e.framework.core.model.selector.Selector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractPresentationElement<S extends Selector, L extends Location> {
    private final S selector;
    private final L location;
}
