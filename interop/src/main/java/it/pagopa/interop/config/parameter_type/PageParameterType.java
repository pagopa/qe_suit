package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.domain.web.pages.dev_tools.DebugClientAssertionPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PageParameterType {
    private static final String DEBUG_CLIENT_ASSERTION_PAGE_ALIAS =
            "DebugClientAssertion|Debug Client Assertion|Debug client assertion";

    private static final String PAGE_TYPES = DEBUG_CLIENT_ASSERTION_PAGE_ALIAS;

    private final WebPresentationGateway webPresentationGateway;

    @ParameterType(PAGE_TYPES)
    public Page page(String page) {
        String normalized = normalize(page);

        if (containsAlias(DEBUG_CLIENT_ASSERTION_PAGE_ALIAS, normalized)) {
            return webPresentationGateway.bind(DebugClientAssertionPage.class);
        }

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