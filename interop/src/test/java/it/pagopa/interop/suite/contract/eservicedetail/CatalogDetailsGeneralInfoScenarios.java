package it.pagopa.interop.suite.contract.eservicedetail;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;

import java.util.stream.Stream;

import static it.pagopa.interop.suite.contract.eservicedetail.EServiceDetailScenarios.*;

/**
 * Scenarios 23-47: general information section.
 */
final class CatalogDetailsGeneralInfoScenarios {

    private CatalogDetailsGeneralInfoScenarios() {
    }

    static Stream<WebScenario<EServiceDetailPage>> of(EService eService, EServiceDescriptor descriptor) {
        return Stream.of(
                text("23", "Titolo sezione Informazioni generali", EServiceDetailPage::generalInfoTitle, "Informazioni generali"),
                text("24", "Label produttore", EServiceDetailPage::producerLabel, "Erogatore"),
                text("25", "Valore produttore", EServiceDetailPage::producerValue, Tenant.COMUNE_DI_MILANO.getName()),
                absent("26", "Riga Template in uso", EServiceDetailPage::templateInUseLabel),
                text("27", "Label versione", EServiceDetailPage::descriptorVersionLabel, "Stai vedendo la versione"),
                text("28", "Valore versione", EServiceDetailPage::descriptorVersionValue, descriptor.getVersion()),
                text("29", "Label dati personali", EServiceDetailPage::personalDataLabel, "Eroga dati personali"),
                text("30", "Valore dati personali", EServiceDetailPage::personalDataValue, "No"),
                text("31", "Label scambio dati", EServiceDetailPage::exchangeTypeLabel, "Scambio dati"),
                text("32", "Valore scambio dati", EServiceDetailPage::exchangeTypeValue, "Sincrono"),
                text("33", "Label descrizione e-service", EServiceDetailPage::eserviceDescriptionLabel, "Descrizione dell'e-service"),
                text("34", "Valore descrizione e-service", EServiceDetailPage::eserviceDescriptionValue, eService.getDescription()),
                text("35", "Label descrizione versione", EServiceDetailPage::descriptorDescriptionLabel, "Descrizione della versione"),
                text("36", "Valore descrizione versione", EServiceDetailPage::descriptorDescriptionValue, descriptor.getDescription()),
                text("37", "Titolo Fruizione tramite delega", EServiceDetailPage::delegationSectionTitle, "Fruizione tramite delega"),
                text("38", "Label autorizzazione", EServiceDetailPage::consumerDelegableLabel, "Autorizzazione"),
                text("39", "Valore autorizzazione", EServiceDetailPage::consumerDelegableValue, "Sì"),
                text("40", "Label autorizzazione client", EServiceDetailPage::clientAccessDelegableLabel, "Autorizzazione all’associazione dei client"),
                text("41", "Valore autorizzazione client", EServiceDetailPage::clientAccessDelegableValue, "Sì"),
                present("42", "Azione Vedi i dettagli tecnici dell’e-service", EServiceDetailPage::showTechnicalDetailsAction),
                text("43", "Azione Vedi i dettagli tecnici dell’e-service", EServiceDetailPage::showTechnicalDetailsAction, "Vedi i dettagli tecnici dell’e-service"),
                enabledElement("44", "Azione Vedi i dettagli tecnici dell’e-service", EServiceDetailPage::showTechnicalDetailsAction),
                absent("45", "Azione Vedi i dettagli sullo scambio asincrono", EServiceDetailPage::showAsyncExchangeDetailsAction),
                present("46", "Azione Visualizza i contatti dell’erogatore", EServiceDetailPage::showProducerContactsAction),
                notVisible("47", "Drawer specifiche tecniche / scambio asincrono / contatti", EServiceDetailPage::openDrawer)
        );
    }
}

