package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.suit.component.Breadcrumb;
import it.pagopa.interop.web.infrastructure.suit.component.Button;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

@Url("${interop.web.catalog}")
public interface EServiceDetailPage extends Page {

    @XPath(".//h1")
    Readable<String> eServiceName();

    Breadcrumb breadcrumb();

    @XPath(".//*[contains(normalize-space(.), 'Richiedi fruizione')]")
    Button requestAgreementButton();

    @Override
    default void assertLoaded() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(eServiceName().read())
                    .as("Il nome dell'eservice non deve essere vuoto")
                    .isNotEmpty();

            softly.assertThat(requestAgreementButton().isDisabled())
                    .as("Il bottone 'Richiedi Fruizione' non deve essere disabilitato")
                    .isFalse();

            softly.assertThat(isBreadcrumbCorrect())
                    .as("Ordine o contenuto del breadcrumb non conforme al figma")
                    .isTrue();
        });
    }

    default boolean isBreadcrumbCorrect() {
        boolean ret = true;
        List<String> breadcrumbItems = this.breadcrumb().getBreadcrumbItems().stream()
                .map(it.frontend.e2e.framework.core.capability.core.Readable::read)
                .toList();
        List<String> figmaBreadcrumbItems = List.of(
                "Catalogo e-service",
                "Visualizza e-service"
        );

        // check 1:1 tra
        //      - elementi trovati da UI (breadCrumbItems) e
        //      - desiderata imposta su Figma (figmaBreadcrumbItems)
        for(int i=0; i<breadcrumbItems.size(); ++i) {
            String breadcrumbItem = breadcrumbItems.get(i);
            String uiBreadcrumbItem = figmaBreadcrumbItems.get(i);
            if(breadcrumbItem.compareTo(uiBreadcrumbItem)!=0) {
                ret = false;
                break;
            }
        }
        return ret;
    }
}
