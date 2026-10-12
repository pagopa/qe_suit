package it.frontend.e2e.framework.web.domain;

import it.frontend.e2e.framework.web.config.WebSuiteContext;
import it.frontend.e2e.framework.web.model.location.Url;

public interface Page extends AbstractPage {

    default void assertLoaded() {
        throw new UnsupportedOperationException(
                "Method assertLoaded() not implemented for " + this.getClass().getName()
        );
    }

    default Url getUrl() {
        var annotation = findUrlAnnotation(this.getClass());

        if (annotation == null) {
            throw new IllegalStateException(
                    "@Url mancante per " + this.getClass().getName()
            );
        }

        return WebSuiteContext.getConfiguration()
                .getLocationResolver()
                .resolve(annotation.value());
    }

    private static it.frontend.e2e.framework.annotation.location.web.Url findUrlAnnotation(Class<?> type) {
        var annotation = type.getAnnotation(it.frontend.e2e.framework.annotation.location.web.Url.class);

        if (annotation != null) {
            return annotation;
        }

        for (Class<?> interfaceType : type.getInterfaces()) {
            annotation = findUrlAnnotation(interfaceType);

            if (annotation != null) {
                return annotation;
            }
        }

        Class<?> superclass = type.getSuperclass();

        if (superclass != null && superclass != Object.class) {
            return findUrlAnnotation(superclass);
        }

        return null;
    }
}