<<<<<<<< HEAD:common/src/main/java/it/pagopa/application/context/LastApiResponseStore.java
package it.pagopa.application.context;
========
package it.pagopa.kernel.context;
>>>>>>>> b64eb6e3 (refactor: [QA-15573] migrate classi comune dal modulo interop al modulo common e refactor degli imports.):common/src/main/java/it/pagopa/kernel/context/LastApiResponseStore.java

import it.pagopa.infrastructure.response.ApiResponse;

public interface LastApiResponseStore {
    ApiResponse getLastResponse();

    void setLastResponse(ApiResponse lastResponse);
}
