package it.frontend.e2e.framework.web.capability.core;

import java.util.List;

/**
 * Opt-in request capture for web components. Existing clickable contracts are unchanged.
 */
public interface RequestCapturingClickable extends it.frontend.e2e.framework.core.capability.core.Clickable {

    /**
     * Clicks once and collects full URLs of newly completed HTTP(S) fetch/XHR resources.
     * Collection ends after one second without new matching entries, within the adapter's
     * default wait timeout. Duplicate URLs are retained. Background requests may be included;
     * delayed or still-pending requests may be omitted. This does not capture request-start
     * order, redirect hops, headers, or response bodies. Full-page navigation is unsupported.
     *
     * @return the observed URLs, including query parameters
     * @throws org.openqa.selenium.TimeoutException if no request is observed or collection
     *         does not become quiet before the deadline
     */
    List<String> clickAndGetRequestUrls();
}