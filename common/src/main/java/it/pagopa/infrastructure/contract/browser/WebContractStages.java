package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.domain.Page;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

public interface WebContractStages {

    interface UserStage {
        <P extends Page> PageStage<P> on(Class<P> pageType);
    }

    interface PageStage<P extends Page> {
        Stream<DynamicTest> tests(Stream<? extends WebScenario<P>> scenarios);
    }
}
