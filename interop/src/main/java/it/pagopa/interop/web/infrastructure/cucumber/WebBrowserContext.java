package it.pagopa.interop.new_arch.web.infrastructure.cucumber;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class WebBrowserContext {

    @Getter @Setter
    private Url currentUrl;

    public void reset(){
        currentUrl = null;
    }
}
