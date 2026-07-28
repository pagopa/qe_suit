package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${interop.web.catalog}")
public interface EServiceDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> eServiceName();

    @Override
    default void assertLoaded() {
        Page.super.assertLoaded();
    }
}
