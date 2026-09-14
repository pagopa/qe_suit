package it.pagopa.send.web.login.infrastructure.page;

import it.frontend.e2e.framework.web.domain.AbstractPage;

public interface AbstractComunePickerPage extends AbstractPage {
    void selectComune(String comune);
}
