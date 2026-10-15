package it.pagopa.interop.suite.contract.eservicedetail;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;
import org.assertj.core.api.Assertions;

import java.util.stream.Stream;

import static it.pagopa.interop.suite.contract.eservicedetail.EServiceDetailScenarios.*;

/**
 * Scenarios 01-22: header, primary actions, alerts, tabs.
 */
final class CatalogDetailsHeaderScenarios {

    private CatalogDetailsHeaderScenarios() {
    }

    static Stream<WebScenario<EServiceDetailPage>> of(EService eService, EServiceDescriptor descriptor) {
        return Stream.of(
                // ---- Intestazione
                present("01", "h1 titolo pagina", EServiceDetailPage::pageTitle),
                visible("02", "h1 titolo pagina", EServiceDetailPage::pageTitle),
                text("03", "h1 titolo pagina", EServiceDetailPage::pageTitle, eService.getName()),
                present("04", "Torna al catalogo", EServiceDetailPage::backToCatalogAction),
                text("05", "Torna al catalogo", EServiceDetailPage::backToCatalogAction, "Torna al catalogo"),
                // h2 rendered in uppercase by the frontend (text-transform)
                text("06", "Label versione", EServiceDetailPage::versionHeaderLabel, "VERSIONE"),
                text("07", "Pulsante versione", EServiceDetailPage::versionShortcutButton, descriptor.getVersion()),
                enabledButton("08", "Pulsante versione", EServiceDetailPage::versionShortcutButton),
                text("09", "Chip di stato descrittore", EServiceDetailPage::descriptorStatusChip, "attiva"),

                // ---- Azioni primarie
                present("11", "Richiedi fruizione", EServiceDetailPage::agreementButton),
                visible("12", "Richiedi fruizione", EServiceDetailPage::agreementButton),
                text("13", "Richiedi fruizione", EServiceDetailPage::agreementButton, "Richiedi fruizione"),
                enabledButton("14", "Richiedi fruizione", EServiceDetailPage::agreementButton),
                absentAll("15", "Visualizza richiesta, Completa richiesta, Aggiorna a nuova versione",
                        EServiceDetailPage::inspectAgreementButton,
                        EServiceDetailPage::completeAgreementButton,
                        EServiceDetailPage::upgradeToNewVersionButton),

                // ---- Alert
                scenario("16", "Alert di stato descrittore", "Presenza nel DOM",
                        page -> Assertions.assertThat(page.alerts())
                                .as(label("16", "Alert di stato descrittore"))
                                .isEmpty()),
                scenario("17", "Alert portachiavi mancante / chiavi mancanti", "Presenza nel DOM",
                        page -> Assertions.assertThat(page.alerts().stream().map(a -> a.message().read()).toList())
                                .as(label("17", "Alert portachiavi mancante / chiavi mancanti"))
                                .noneMatch(message -> message.contains("portachiavi"))),

                // ---- Tab
                text("18", "Tab Dettaglio e-service", EServiceDetailPage::eserviceDetailTab, "Dettaglio e-service"),
                attribute("19", "Tab Dettaglio e-service", EServiceDetailPage::eserviceDetailTab, "aria-selected", "true"),
                text("20", "Tab Template finalità collegati", EServiceDetailPage::purposeTemplatesTab, "Template finalità collegati"),
                attribute("21", "Tab Template finalità collegati", EServiceDetailPage::purposeTemplatesTab, "aria-selected", "false"),
                notVisible("22", "Pannello tab Template finalità collegati", EServiceDetailPage::purposeTemplatesTabPanel)
        );
    }
}

