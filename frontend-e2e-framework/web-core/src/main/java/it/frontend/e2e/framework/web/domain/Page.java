package it.frontend.e2e.framework.web.domain;

import it.frontend.e2e.framework.web.config.WebSuiteContext;
import it.frontend.e2e.framework.web.model.location.Url;

public interface Page extends AbstractPage {
    default void assertLoaded() {
        throw new UnsupportedOperationException("Method assertLoaded() not implemented for " + this.getClass().getName());
    }

    default Url getUrl() {
        var annotation = this.getClass().getAnnotation(it.frontend.e2e.framework.annotation.location.web.Url.class);
        if (annotation == null)
            throw new IllegalStateException("@Url mancante su " + Page.class.getName());

        return WebSuiteContext.getConfiguration().getLocationResolver().resolve(annotation.value());
    }
}
