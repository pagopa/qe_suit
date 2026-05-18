package it.pagopa.send.domain.web.commons.pages.login;

import it.frontend.e2e.framework.web.domain.AbstractPage;

public interface AbstractComunePickerPage extends AbstractPage {
    void selectComune(String comune);
}
