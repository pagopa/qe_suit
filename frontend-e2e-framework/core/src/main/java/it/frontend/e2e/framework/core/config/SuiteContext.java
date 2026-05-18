package it.frontend.e2e.framework.core.config;

public class SuiteContext {
    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<SuiteConfiguration> configuration = new ThreadLocal<>();

    public SuiteContext() {}

    @SuppressWarnings("rawtypes")
    public static SuiteConfiguration getConfiguration() {
        SuiteConfiguration config = configuration.get();
        if (config == null) {
            throw new IllegalStateException("SuiteContext is not initialized");
        }
        return config;
    }

    @SuppressWarnings("rawtypes")
    public static void setConfiguration(SuiteConfiguration config) {
        configuration.set(config);
    }

    public static void reset() {
        configuration.remove();
    }
}