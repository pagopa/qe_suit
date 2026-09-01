package it.pagopa.infrastructure.contract.browser;

import java.util.Objects;
import java.util.function.Consumer;

public record WebScenario<P>(
        String name,
        Consumer<P> action,
        Consumer<P> assertion
) {
    public WebScenario {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(assertion, "assertion must not be null");
    }
}
