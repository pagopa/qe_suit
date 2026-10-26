package it.pagopa.utils.suit;

import it.frontend.e2e.framework.web.model.WebPresentationElement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SuitUtils {

    private SuitUtils() {
        throw new UnsupportedOperationException("Questa è una classe di utility e non può essere istanziata");
    }

    public static void debug(WebPresentationElement element) {
        if (element == null) {
            log.info("Element: null");
            return;
        }

        final var classes = element.getClasses();
        final var classSummary = classes.isEmpty() ? "none" : String.join(", ", classes);
        final var attributes = element.getAttributes();

        log.info("Element: {}\nCSS classes: {}", element.getTag(), classSummary);

        if (attributes != null && !attributes.isEmpty()) {
            attributes.forEach((key, value) -> log.info("Attribute: {} = {}", key, value));
        }
    }
}
