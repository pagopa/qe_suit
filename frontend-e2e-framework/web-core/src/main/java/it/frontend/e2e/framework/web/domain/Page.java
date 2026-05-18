package it.frontend.e2e.framework.web.domain;

public interface Page extends AbstractPage {
    default void assertLoaded() {
        throw new UnsupportedOperationException("Method assertLoaded() not implemented for " + this.getClass().getName());
    }
}
