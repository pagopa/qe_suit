package it.frontend.e2e.framework.core.model.location.resolver;

import it.frontend.e2e.framework.core.model.location.Location;

@FunctionalInterface
public interface ILocationResolver<L extends Location> {
    L resolve(String location);
}
