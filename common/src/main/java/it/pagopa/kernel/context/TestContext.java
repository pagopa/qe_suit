<<<<<<<< HEAD:common/src/main/java/it/pagopa/application/context/TestContext.java
package it.pagopa.application.context;

import it.pagopa.application.TestKind;
========
package it.pagopa.kernel.context;

import it.pagopa.kernel.domain.TestKind;
>>>>>>>> b64eb6e3 (refactor: [QA-15573] migrate classi comune dal modulo interop al modulo common e refactor degli imports.):common/src/main/java/it/pagopa/kernel/context/TestContext.java

import java.util.List;

public interface TestContext {
    TestKind getCurrentTestKind();
    void setCurrentTestKind(TestKind currentTestKind);
    void addEventualConsistencyError(String error);
    List<String> getEventualConsistencyErrors();
}
