package it.pagopa.interop.common.infrastructure.context.inmemory;

import it.pagopa.interop.common.infrastructure.context.CurrentTestKind;
import it.pagopa.interop.common.kernel.domain.TestKind;

import java.util.concurrent.atomic.AtomicReference;

public class InMemoryCurrentTestKind implements CurrentTestKind {
    private final ThreadLocal<TestKind> currentTestKind = new ThreadLocal<>();
    private final AtomicReference<TestKind> defaultTestKind = new AtomicReference<>(TestKind.CONTRACT);

    @Override
    public TestKind getCurrentTestKind() {
        TestKind testKind = currentTestKind.get();
        return testKind != null ? testKind : defaultTestKind.get();
    }

    @Override
    public void setCurrentTestKind(TestKind currentTestKind) {
        this.currentTestKind.set(currentTestKind);
    }
}
