package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Page;

public class PageParameterType {
    private final static String DEBUG_CLIENT_ASSERTION_PAGE_ALIAS = "DebugClientAssertion|Debug Client Assertion|Debug client assertion";
    private final static String PAGE_TYPES = DEBUG_CLIENT_ASSERTION_PAGE_ALIAS;

    @ParameterType(PAGE_TYPES)
    public Page page(String page) {
        return null;
    }
}
