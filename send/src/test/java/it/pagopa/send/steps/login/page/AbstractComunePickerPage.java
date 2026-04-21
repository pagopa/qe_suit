package it.pagopa.send.steps.login.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;

@Url("")
public interface AbstractComunePickerPage extends Page {
    void selectComune(String comune);
}
