package it.pagopa.send.domain.web.pages.mittente.timeline;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${url.notifiche.mittente.dashboard}/${iun}/dettaglio/timeline")
public interface TimelineDetailsPage extends Page {

    InvioInCorsoComponent invioInCorsoComponent();

    @Override
    default void assertLoaded() {
    }
}
