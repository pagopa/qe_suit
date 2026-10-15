package it.pagopa.interop.suite.contract.eservicedetail;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;

import java.util.stream.Stream;

import static it.pagopa.interop.suite.contract.eservicedetail.EServiceDetailScenarios.*;

/**
 * Scenarios 48-70: purpose templates, Signal Hub, thresholds/attributes, unexpected catalog-list elements.
 */
final class CatalogDetailsSectionsScenarios {

    private static final String PURPOSE_TEMPLATES_DESCRIPTION = "Questa funzionalità ti guida durante l'analisi del rischio con annotazioni, documenti di supporto e alcune risposte precompilate. Il modello agevolato è reso disponibile per il riuso da altri enti e può essere usato senza vincoli.";
    private static final String SIGNAL_HUB_DESCRIPTION = "Signal Hub è una soluzione integrata che informa i fruitori quando i dati dell'e-service vengono modificati.";
    private static final String ATTRIBUTES_DESCRIPTION = "Qui trovi le soglie e gli attributi che sono richiesti per la fruizione di questo e-service.";

    private CatalogDetailsSectionsScenarios() {
    }

    static Stream<WebScenario<EServiceDetailPage>> of() {
        return Stream.of(
                // ---- Compilazione agevolata
                text("48", "Titolo sezione Compilazione agevolata", EServiceDetailPage::purposeTemplatesTitle, "Compilazione agevolata della finalità"),
                text("49", "Descrizione sezione Compilazione agevolata", EServiceDetailPage::purposeTemplatesDescription, PURPOSE_TEMPLATES_DESCRIPTION),
                present("50", "Pulsante Visualizza i template collegati", EServiceDetailPage::viewLinkedPurposeTemplatesButton),
                text("51", "Pulsante Visualizza i template collegati", EServiceDetailPage::viewLinkedPurposeTemplatesButton, "Visualizza i template collegati"),
                enabledButton("52", "Pulsante Visualizza i template collegati", EServiceDetailPage::viewLinkedPurposeTemplatesButton),

                // ---- Signal Hub
                text("53", "Titolo sezione Signal Hub", EServiceDetailPage::signalHubTitle, "Signal Hub"),
                text("54", "Descrizione sezione Signal Hub", EServiceDetailPage::signalHubDescription, SIGNAL_HUB_DESCRIPTION),
                present("55", "Link soluzione integrata", EServiceDetailPage::signalHubLink),
                text("56", "Link soluzione integrata", EServiceDetailPage::signalHubLink, "soluzione integrata"),
                text("57", "Label disponibilità", EServiceDetailPage::signalHubAvailabilityLabel, "Disponibilità del servizio"),
                text("58", "Valore disponibilità", EServiceDetailPage::signalHubAvailabilityValue, "No, non è disponibile"),

                // ---- Soglie e attributi
                text("59", "Titolo sezione Soglie e attributi", EServiceDetailPage::attributesSectionTitle, "Soglie e attributi"),
                text("60", "Descrizione sezione Soglie e attributi", EServiceDetailPage::attributesSectionDescription, ATTRIBUTES_DESCRIPTION),
                text("61", "Titolo Soglie di chiamate API", EServiceDetailPage::thresholdsTitle, "Soglie di chiamate API"),
                text("62", "Label soglia per fruitore", EServiceDetailPage::dailyCallsPerConsumerLabel, "Soglia giornaliera per fruitore"),
                text("63", "Valore soglia per fruitore", EServiceDetailPage::dailyCallsPerConsumerValue, "1"),
                text("64", "Label soglia totale", EServiceDetailPage::dailyCallsTotalLabel, "Soglia giornaliera totale"),
                text("65", "Valore soglia totale", EServiceDetailPage::dailyCallsTotalValue, "10"),
                absent("66", "Sezione Soglie di chiamate API personalizzate", EServiceDetailPage::customizedThresholdsTitle),
                absentAll("67", "Gruppi attributi Certificati / Verificati / Dichiarati",
                        EServiceDetailPage::certifiedAttributesTitle,
                        EServiceDetailPage::verifiedAttributesTitle,
                        EServiceDetailPage::declaredAttributesTitle),
                text("68", "Titolo sezione interna attributi", EServiceDetailPage::attributesTitle, "Attributi"),
                text("68b", "Messaggio nessun attributo richiesto", EServiceDetailPage::noAttributesRequiredMessage, "Questo e-service non richiede attributi"),

                // ---- Elementi non previsti (lista catalogo)
                absent("69", "h1 Catalogo degli e-service", EServiceDetailPage::catalogListTitle),
                absentAll("70", "Campi Cerca per nome e Cerca per erogatore",
                        EServiceDetailPage::searchByNameField,
                        EServiceDetailPage::searchByProducerField)
        );
    }
}

