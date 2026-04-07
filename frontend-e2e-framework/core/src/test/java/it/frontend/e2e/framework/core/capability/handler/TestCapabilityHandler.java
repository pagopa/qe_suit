package it.frontend.e2e.framework.core.capability.handler;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.core.TestCapability;
import it.frontend.e2e.framework.core.model.TestElement;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class TestCapabilityHandler extends AbstractCapabilityHandler<TestCapability> {

    public TestCapabilityHandler(TestCapability capabilityImpl) {
        super(capabilityImpl);
    }

    // Helper method per creare un'implementazione mock di TestCapability
    public static TestCapability createMock() {
        return new TestCapability() {
            @Override
            public void action() {}

            @Override
            public List<?> getList() {
                return List.of();
            }

            @Override
            public Optional<?> getOptional() {
                return Optional.empty();
            }

            @Override
            public Optional<TestElement> get() {
                return Optional.empty();
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T handle(Method method, Object[] args, CapabilityScope scope) {

        return switch (method.getName()) {
            case "action" -> {
                System.out.println("Action method called");
                yield null;
            }
            case "getList" -> {
                System.out.println("getList method called");
                yield (T) List.of();
            }
            case "getOptional" -> {
                System.out.println("getOptional method called");
                yield (T) Optional.empty();
            }
            default -> throw new UnsupportedOperationException("Method not supported: " + method.getName());
        };
    }
}