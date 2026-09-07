package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.Optional;

@Url("${interop.web.catalog}/${agreementId}")
public interface EServiceAgreementPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @XPath(".//div[contains(@class,'MuiAlert-message') and contains(text(),'Questa versione dell’e-service è obsoleta, ma è ancora attiva. È disponibile una nuova versione.')]")
    Optional<Readable<String>> banner1();

    @XPath(".//div[contains(@class,'MuiAlert-message') and contains(text(),'Questa versione dell’e-service è obsoleta, ma è ancora attiva.')]")
    Optional<Readable<String>> banner2();


    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText()).as("Breadcrumbs last item text").isEqualTo("Gestisci richiesta");
            softly.assertThat(pageTitle().readAndAssert(eServiceName -> Assertions.assertThat(eServiceName).as("Page title is not blank").isNotBlank()));
        });
    }

    default void assertLoadedBanner1() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText())
                    .as("Breadcrumbs last item text")
                    .isEqualTo("Gestisci richiesta");

            softly.assertThat(
                    banner1().isPresent()
            );

            softly.assertThat(
                    banner1().get().readAndAssert(text ->
                            Assertions.assertThat(text)
                                    .as("Banner1 text")
                                    .contains("Questa versione dell’e-service è obsoleta, ma è ancora attiva. È disponibile una nuova versione.")
                    )
            );
        });
    }

    default void assertLoadedBanner2() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText())
                    .as("Breadcrumbs last item text")
                    .isEqualTo("Gestisci richiesta");

            softly.assertThat(
                    banner2().isPresent()
            );

            softly.assertThat(
                    banner2().get().readAndAssert(text ->
                            Assertions.assertThat(text)
                                    .as("Banner1 text")
                                    .contains("Questa versione dell’e-service è obsoleta, ma è ancora attiva.")
                    )
            );
        });
    }

    default void assertLoadedNoBanners() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(breadcrumbs().getLastItemText())
                    .as("Breadcrumbs last item text")
                    .isEqualTo("Gestisci richiesta");

            softly.assertThat(banner1().isPresent())
                    .as("Banner1 should not be present")
                    .isFalse();

            softly.assertThat(banner2().isPresent())
                    .as("Banner2 should not be present")
                    .isFalse();
        });
    }
}
