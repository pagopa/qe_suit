package it.frontend.e2e.framework.core.logging;

public interface ILogger {
    void logAction(String selector, String actionType, String details);
    void logNavigation(String toPage);
    void logWait(String selector, long durationMs, boolean success);
    void logError(String context, Throwable throwable);
    void logDebug(String message);
    void logInfo(String message);
}
