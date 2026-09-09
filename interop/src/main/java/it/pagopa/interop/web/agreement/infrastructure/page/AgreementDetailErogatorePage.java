package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.common.eservice.domain.EService;
import org.openqa.selenium.Keys;

@Url("${interop.web.agreement-detail-erogatore}/${agreementId}")
public interface AgreementDetailErogatorePage extends Page {

}

