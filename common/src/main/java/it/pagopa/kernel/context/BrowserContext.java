<<<<<<<< HEAD:common/src/main/java/it/pagopa/application/context/BrowserContext.java
package it.pagopa.application.context;
========
package it.pagopa.kernel.context;
>>>>>>>> b64eb6e3 (refactor: [QA-15573] migrate classi comune dal modulo interop al modulo common e refactor degli imports.):common/src/main/java/it/pagopa/kernel/context/BrowserContext.java

import it.frontend.e2e.framework.web.model.location.Url;

public interface BrowserContext {
    Url getCurrentUrl();
    void setCurrentUrl(Url url);
    void reset();
}
