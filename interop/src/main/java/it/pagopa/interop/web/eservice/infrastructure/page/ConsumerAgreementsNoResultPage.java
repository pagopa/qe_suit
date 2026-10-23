package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;

@Url("${interop.web.base-url}/fruizione/richieste?eservicesIds=%5B%5B\"no-result\"%2C\"11111111-1111-1111-1111-111111111111\"%5D%5D")
public interface ConsumerAgreementsNoResultPage extends ConsumerAgreementsListPage {

    @XPath("//*[contains(@class,'MuiAlert-message') and .='La ricerca corrente non ha prodotto risultati']")
    Label noResultMessage();
}
