package it.frontend.e2e.framework.core.capability.core;

import it.frontend.e2e.framework.core.model.TestElement;
import it.frontend.e2e.framework.core.model.TestLocation;
import it.frontend.e2e.framework.core.model.TestSelector;

import java.util.List;
import java.util.Optional;

public interface TestCapability extends Gettable<TestSelector, TestLocation, TestElement> {
    void action();
    List<?> getList();
    Optional<?> getOptional();
}
