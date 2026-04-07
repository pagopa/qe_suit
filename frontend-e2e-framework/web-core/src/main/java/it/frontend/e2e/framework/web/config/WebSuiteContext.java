package it.frontend.e2e.framework.web.config;

import it.frontend.e2e.framework.core.config.SuiteContext;

import java.util.Objects;

public class WebSuiteContext extends SuiteContext {
    private WebSuiteContext() {}

    public static void initialize(WebSuiteConfiguration configuration) {
        Objects.requireNonNull(configuration, "configuration must not be null");

        synchronized (WebSuiteContext.class) {
            if (WebSuiteContext.configuration != null) {
                throw new IllegalStateException("WebSuiteContext is already initialized");
            }
            SuiteContext.configuration = configuration;
        }
    }

    public static WebSuiteConfiguration getConfiguration() {
        return (WebSuiteConfiguration) SuiteContext.getConfiguration();
    }

    public static void reset() {
        synchronized (WebSuiteContext.class) {
            SuiteContext.configuration = null;
        }
    }
}
