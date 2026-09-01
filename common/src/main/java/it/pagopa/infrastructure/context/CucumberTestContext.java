package it.pagopa.infrastructure.context;

import it.pagopa.application.context.TestContext;
import it.pagopa.application.TestKind;

import java.util.ArrayList;
import java.util.List;

public class CucumberTestContext implements TestContext {

    private TestKind currentTestKind = TestKind.FLOW;

    private final List<String> eventualConsistencyErrors = new ArrayList<>();

    @Override
    public void addEventualConsistencyError(String error) {
        eventualConsistencyErrors.add(error);
    }

    @Override
    public TestKind getCurrentTestKind() {
        return currentTestKind;
    }

    @Override
    public void setCurrentTestKind(TestKind currentTestKind) {
        this.currentTestKind = currentTestKind;
    }

    @Override
    public List<String> getEventualConsistencyErrors() {
        return eventualConsistencyErrors;
    }
}
