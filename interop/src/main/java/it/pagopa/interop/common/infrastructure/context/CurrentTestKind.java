package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.kernel.domain.TestKind;

public interface CurrentTestKind {
    TestKind getCurrentTestKind();

    void setCurrentTestKind(TestKind currentTestKind);
}
