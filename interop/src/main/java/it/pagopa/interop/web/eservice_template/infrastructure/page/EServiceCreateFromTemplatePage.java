package it.pagopa.interop.web.eservice_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Drawer;
import it.pagopa.infrastructure.suit.component.Snackbar;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/erogazione/e-service/template-eservice/{eServiceTemplateId}/crea")
public interface EServiceCreateFromTemplatePage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                name -> !name.isBlank(),
                "Producer E-Service Template Detail Page is not loaded yet. Page title is blank"
        );
    }
}

