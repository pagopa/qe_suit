package it.frontend.e2e.framework.web.adapter.model;

/** Timeouts in seconds; 10/60 mirror the legacy adapter behavior. */
public record WebWaitSettings(long defaultWaitTimeoutSeconds,
                              long retryWaitTimeoutSeconds,
                              long existsTimeoutSeconds) {
    public WebWaitSettings {
        if (defaultWaitTimeoutSeconds < 0 || retryWaitTimeoutSeconds < 0 || existsTimeoutSeconds < 0) {
            throw new IllegalArgumentException("Wait timeouts cannot be negative");
        }
    }

    public static WebWaitSettings defaults() {
        return new WebWaitSettings(10, 60, 0);
    }
}
