package it.pagopa.send.web.campagne.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.campagne.infrastructure.page.component.CampagneTable;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.mittente.infrastructure.page.component.MittentiSidebar;
import it.pagopa.send.web.notification_search.infrastructure.suit.NotificationSearchPage;
import org.assertj.core.api.Assertions;

import java.util.Map;
import java.util.Optional;

@Url("${url.notifiche.mittente.campaigns}#selfCareToken=${token.mittente}")
public interface CampagnePage extends Page {
    CampagneTable table();
    @XPath("//*[@data-testid='empyState']//h6[contains(@class, 'MuiTypography-root')]")
    Readable<String> emptyStateLabel();
}
