package it.pagopa.infrastructure.contract.browser;

import it.frontend.e2e.framework.web.domain.Page;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

public interface WebContractStages {

    interface UserStage {
        <P extends Page> PageStage<P> on(Class<P> pageType);

        /**
         * Come {@link #on(Class)}, ma risolve i placeholder {@code ${...}} nell'{@code @Url} della
         * pagina con {@code pathParams} (stesso meccanismo di {@code Page.navigateTo(String...)}),
         * per bindare direttamente una pagina che richiede parametri (es. un id nel path).
         */
        <P extends Page> PageStage<P> on(Class<P> pageType, String... pathParams);
    }

    interface PageStage<P extends Page> {
        Stream<DynamicTest> tests(Stream<? extends WebScenario<P>> scenarios);
    }
}
