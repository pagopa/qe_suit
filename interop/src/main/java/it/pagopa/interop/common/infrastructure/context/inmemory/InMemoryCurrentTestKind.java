package it.pagopa.interop.common.infrastructure.context.inmemory;

import it.pagopa.interop.common.infrastructure.context.CurrentTestKind;
import it.pagopa.interop.common.kernel.domain.TestKind;

public class InMemoryCurrentTestKind implements CurrentTestKind {
    private TestKind currentTestKind = TestKind.CONTRACT;

    @Override
    public TestKind getCurrentTestKind() {
        return currentTestKind;
    }

    @Override
    public void setCurrentTestKind(TestKind currentTestKind) {
        this.currentTestKind = currentTestKind;
    }
}
