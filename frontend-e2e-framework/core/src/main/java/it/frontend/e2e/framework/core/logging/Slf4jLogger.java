package it.frontend.e2e.framework.core.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLogger implements ILogger {
    private static final Logger logger = LoggerFactory.getLogger("E2E-Framework");

    @Override
    public void logAction(String selector, String actionType, String details) {
        String detailsStr = details != null && !details.isBlank() ? " | Details: " + details : "";
        logger.info("[ACTION] Type: {}{} | {}", actionType, detailsStr, selector);
    }

    @Override
    public void logNavigation(String toPage) {
        logger.info("[NAVIGATION] {}", toPage);
    }

    @Override
    public void logWait(String selector, long durationMs, boolean success) {
        String status = success ? "SUCCESS" : "TIMEOUT";
        logger.info("[WAIT] {} | Status: {} | Duration: {}ms", selector, status, durationMs);
    }

    @Override
    public void logError(String context, Throwable throwable) {
        logger.error("{}", context, throwable);
    }

    @Override
    public void logDebug(String message) {
        logger.debug("{}", message);
    }

    @Override
    public void logInfo(String message) {
        logger.info("{}", message);
    }
}
