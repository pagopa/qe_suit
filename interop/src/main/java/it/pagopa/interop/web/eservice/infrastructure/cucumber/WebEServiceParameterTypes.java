package it.pagopa.interop.web.eservice.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.debug_client_assertion.infrastructure.page.DebugClientAssertionPage;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class WebEServiceParameterTypes {

    private static final String ESERVICE_CREATION_PAGE_ALIAS =
            "EServiceCreation|EserviceCreation|Creazione eservice|Creazione EService|Creazione Eservice";

    private static final String DEBUG_CLIENT_ASSERTION_PAGE_ALIAS =
            "DebugClientAssertion|Debug Client Assertion";

    private static final String PAGE_TYPES =
            ESERVICE_CREATION_PAGE_ALIAS + "|" + DEBUG_CLIENT_ASSERTION_PAGE_ALIAS;

    private final EServiceCreationPage eServiceCreationPage;
    private final DebugClientAssertionPage debugClientAssertionPage;

    @ParameterType(PAGE_TYPES)
    public Page page(String page) {
        String normalized = normalize(page);

        if (containsAlias(ESERVICE_CREATION_PAGE_ALIAS, normalized)) {
            return eServiceCreationPage;
        }

        if (containsAlias(DEBUG_CLIENT_ASSERTION_PAGE_ALIAS, normalized)) {
            return debugClientAssertionPage;
        }

        throw new IllegalArgumentException("Unsupported page alias: " + page);
    }

    private static boolean containsAlias(
            String aliasesPipeSeparated,
            String targetNormalized
    ) {
        return Arrays.stream(aliasesPipeSeparated.split("\\|"))
                .map(WebEServiceParameterTypes::normalize)
                .anyMatch(alias -> alias.equals(targetNormalized));
    }

    private static String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> getPageAliases() {
        return Arrays.stream(PAGE_TYPES.split("\\|"))
                .toList();
    }
}