package it.pagopa.interop.suite.contract.eservicedetail;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.DomNode;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;
import org.assertj.core.api.Assertions;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Shared scenario builders for the catalog e-service detail page contract.
 */
final class EServiceDetailScenarios {

    private EServiceDetailScenarios() {
    }

    static String label(String id, String element) {
        return "CATALOG-DETAILS-" + id + " " + element;
    }

    static String normalize(String value) {
        return value == null ? null : value.replaceAll("\\s+", " ").trim();
    }

    static WebScenario<EServiceDetailPage> scenario(
            String id,
            String element,
            String property,
            Consumer<EServiceDetailPage> assertion
    ) {
        return new WebScenario<>(
                "CATALOG-DETAILS-" + id + " – " + element + " – " + property,
                page -> {
                },
                assertion
        );
    }

    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> present(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Presenza nel DOM",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.PRESENT))
                        .as(label(id, element) + " (atteso: presente)")
                        .isPresent());
    }

    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> visible(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Visibilità",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.VISIBLE))
                        .as(label(id, element) + " (atteso: visibile)")
                        .isPresent());
    }

    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> notVisible(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Visibilità",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.VISIBLE))
                        .as(label(id, element) + " (atteso: non visibile)")
                        .isEmpty());
    }

    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> absent(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Presenza nel DOM",
                page -> Assertions.assertThat(locator.apply(page).get(FindPolicy.PRESENT))
                        .as(label(id, element) + " (atteso: assente)")
                        .isEmpty());
    }

    @SafeVarargs
    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> absentAll(
            String id, String element, Function<EServiceDetailPage, T>... locators
    ) {
        return scenario(id, element, "Presenza nel DOM", page -> {
            for (Function<EServiceDetailPage, T> locator : locators) {
                Assertions.assertThat(locator.apply(page).get(FindPolicy.PRESENT))
                        .as(label(id, element) + " (atteso: assenti)")
                        .isEmpty();
            }
        });
    }

    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> text(
            String id, String element, Function<EServiceDetailPage, T> locator, String expected
    ) {
        return scenario(id, element, "Copy",
                page -> Assertions.assertThat(normalize(locator.apply(page).read()))
                        .as(label(id, element) + " (copy)")
                        .isEqualTo(normalize(expected)));
    }

    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> attribute(
            String id, String element, Function<EServiceDetailPage, T> locator, String attribute, String expected
    ) {
        return scenario(id, element, "Stato iniziale",
                page -> Assertions.assertThat(
                                locator.apply(page).get(FindPolicy.PRESENT)
                                        .map(found -> found.getAttributes().get(attribute))
                                        .orElse(null))
                        .as(label(id, element) + " (attributo " + attribute + ")")
                        .isEqualTo(expected));
    }

    static WebScenario<EServiceDetailPage> enabledButton(
            String id, String element, Function<EServiceDetailPage, Button> locator
    ) {
        return scenario(id, element, "Stato iniziale",
                page -> Assertions.assertThat(locator.apply(page).isDisabled())
                        .as(label(id, element) + " (atteso: abilitato)")
                        .isFalse());
    }

    static <T extends DomNode & Readable<String>> WebScenario<EServiceDetailPage> enabledElement(
            String id, String element, Function<EServiceDetailPage, T> locator
    ) {
        return scenario(id, element, "Stato iniziale",
                page -> {
                    var found = locator.apply(page).get(FindPolicy.PRESENT);
                    Assertions.assertThat(found).as(label(id, element) + " (atteso: presente)").isPresent();
                    Assertions.assertThat(found.get().getAttributes())
                            .as(label(id, element) + " (atteso: abilitato)")
                            .doesNotContainKey("disabled")
                            .doesNotContainEntry("aria-disabled", "true");
                });
    }
}

