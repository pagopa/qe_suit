package it.pagopa.kernel.context;

import it.pagopa.kernel.TestKind;

import java.util.List;

public interface TestContext {
    TestKind getCurrentTestKind();
    void setCurrentTestKind(TestKind currentTestKind);
    void addEventualConsistencyError(String error);
    List<String> getEventualConsistencyErrors();
}
