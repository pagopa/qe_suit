package it.pagopa.interop.common.kernel.context;

import it.pagopa.interop.common.kernel.domain.TestKind;

import java.util.List;

public interface TestContext {
    TestKind getCurrentTestKind();
    void setCurrentTestKind(TestKind currentTestKind);
    void addEventualConsistencyError(String error);
    List<String> getEventualConsistencyErrors();
}
