package it.pagopa.application.context;

import it.pagopa.application.TestKind;

import java.util.List;

public interface TestContext {
    TestKind getCurrentTestKind();
    void setCurrentTestKind(TestKind currentTestKind);
    void addEventualConsistencyError(String error);
    List<String> getEventualConsistencyErrors();
}
