package it.frontend.e2e.framework.web.domain;

public interface Component extends DomNode {
    default void assertLoaded() {
        throw new UnsupportedOperationException("Method assertLoaded() not implemented for " + this.getClass().getName());
    }
}
