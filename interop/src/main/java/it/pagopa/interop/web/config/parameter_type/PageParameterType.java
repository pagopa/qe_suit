package it.pagopa.interop.web.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.page.dev_tools.DevToolsPage;
import it.pagopa.interop.web.page.dev_tools.debug_client_assertion.DebugClientAssertionPage;
import it.pagopa.interop.web.page.eservice.creation.EServiceCreationPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PageParameterType {
    private static final String DEBUG_CLIENT_ASSERTION_PAGE_ALIAS =
            "DebugClientAssertion|Debug Client Assertion|Debug client assertion";
    private static final String DEV_TOOLS_PAGE_ALIAS =
            "DevTools|Dev Tools|Dev tools|Tool per lo sviluppo";
    private static final String ESERVICE_CREATION_PAGE_ALIAS =
            "EServiceCreation|EserviceCreation|Creazione eservice|Creazione EService|Creazione Eservice";

    private static final String PAGE_TYPES = DEBUG_CLIENT_ASSERTION_PAGE_ALIAS + "|" + DEV_TOOLS_PAGE_ALIAS + "|" + ESERVICE_CREATION_PAGE_ALIAS;

    private final DebugClientAssertionPage debugClientAssertionPage;
    private final DevToolsPage devToolsPage;
    private final EServiceCreationPage eServiceCreationPage;

    @ParameterType(PAGE_TYPES)
    public Page page(String page) {
        String normalized = normalize(page);


        if (containsAlias(DEBUG_CLIENT_ASSERTION_PAGE_ALIAS, normalized)) {
            return debugClientAssertionPage;
        }
        if (containsAlias(DEV_TOOLS_PAGE_ALIAS, normalized)) {
            return devToolsPage;
        }
        if (containsAlias(ESERVICE_CREATION_PAGE_ALIAS, normalized))
            return eServiceCreationPage;

        throw new IllegalArgumentException("Unsupported page alias: " + page);
    }

    private static boolean containsAlias(String aliasesPipeSeparated, String targetNormalized) {
        return Arrays.stream(aliasesPipeSeparated.split("\\|"))
                .map(PageParameterType::normalize)
                .anyMatch(alias -> alias.equals(targetNormalized));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> getPageAliases() {
        return Arrays.stream(PAGE_TYPES.split("\\|")).toList();
    }
}