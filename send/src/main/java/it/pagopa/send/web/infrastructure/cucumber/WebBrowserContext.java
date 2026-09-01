package it.pagopa.send.web.infrastructure.cucumber;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.domain.Page;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.send.common.domain.Recipient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class WebBrowserContext {
    private Recipient recipient;
    private Tenant tenant;
    private User currentUser;
    private Page currentPage;
    private Page previousPage;
    private Url currentUrl;
}
