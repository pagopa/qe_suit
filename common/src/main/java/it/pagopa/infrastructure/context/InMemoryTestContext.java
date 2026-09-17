package it.pagopa.infrastructure.context;

import it.pagopa.application.context.TestContext;
import it.pagopa.application.TestKind;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class InMemoryTestContext implements TestContext {

    private final ThreadLocal<TestKind> currentTestKind = new ThreadLocal<>();
    private final AtomicReference<TestKind> defaultTestKind =
            new AtomicReference<>(TestKind.CONTRACT);

    private final ConcurrentLinkedQueue<String> eventualConsistencyErrors =
            new ConcurrentLinkedQueue<>();

    @Override
    public TestKind getCurrentTestKind() {
        TestKind testKind = currentTestKind.get();
        return testKind != null ? testKind : defaultTestKind.get();
    }

    @Override
    public void setCurrentTestKind(TestKind currentTestKind) {
        this.currentTestKind.set(currentTestKind);
    }

    @Override
    public void addEventualConsistencyError(String error) {
        eventualConsistencyErrors.add(error);
    }

    @Override
    public List<String> getEventualConsistencyErrors() {
        return List.copyOf(eventualConsistencyErrors);
    }
}