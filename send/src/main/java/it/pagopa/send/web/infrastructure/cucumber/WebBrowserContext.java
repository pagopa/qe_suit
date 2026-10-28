package it.pagopa.send.web.infrastructure.cucumber;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.domain.Page;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class WebBrowserContext {
    private Page currentPage;
    private Page previousPage;
}
