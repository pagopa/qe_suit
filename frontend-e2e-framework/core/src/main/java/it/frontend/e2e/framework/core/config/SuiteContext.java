package it.frontend.e2e.framework.core.config;

public class SuiteContext {
    @SuppressWarnings("rawtypes")
    protected static volatile SuiteConfiguration configuration;

    @SuppressWarnings("rawtypes")
    public static SuiteConfiguration getConfiguration() {
        if (configuration == null) {
            throw new IllegalStateException("SuiteContext is not initialized");
        }
        return configuration;
    }
}
