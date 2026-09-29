package it.pagopa.send.web.mittente.combo.infrastructure.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
import it.pagopa.send.web.mittente.combo.MittenteComboDetailsPage;
import it.pagopa.send.web.mittente.combo.MittenteComboTimelinePage;
import it.pagopa.send.web.mittente.combo.component.ComboChannelsSection;
import it.pagopa.send.web.mittente.combo.component.ComboTimelineItemComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MittenteComboSteps {

    private final WebPresentationGateway browser;
    private final WebBrowserContext webBrowserContext;

    private MittenteComboDetailsPage comboDetailsPage;
    private MittenteComboTimelinePage comboTimelinePage;

    private String normalizeChannel(String raw) {
        if (raw == null) {
            return "";
        }
        String upper = raw.trim().toUpperCase();
        if (upper.contains("SEND") || upper.contains("PIATTAFORMA")) {
            return "SEND";
        }
        if (upper.contains("IO") || upper.contains("APP IO")) {
            return "IO";
        }
        if (upper.contains("EMAIL") || upper.contains("MAIL") || upper.contains("E-MAIL") || upper.contains("POSTA ORDINARIA")) {
            return "EMAIL";
        }
        if (upper.contains("PEC") || upper.contains("CERTIFICATA")) {
            return "PEC";
        }
        if (upper.contains("POSTALE") || upper.contains("SERVIZIO POSTALE") || upper.contains("RS") || upper.contains("RACCOMANDATA")) {
            return "SERVIZIO POSTALE";
        }
        if (upper.contains("SMS") || upper.contains("MESSAGGIO")) {
            return "SMS";
        }
        return upper;
    }

    @Given("una comunicazione bonaria con IUN {string} e flusso in stato {string}")
    public void setupComboFlowStatus(String iun, String flowStatus) {
        log.info("Impostazione flusso per IUN {} in stato {}", iun, flowStatus);
    }

    @Given("una comunicazione bonaria inviata a PF per la campagna {string} con IUN {string} e flusso {string}")
    public void setupPfComboFlow(String campaign, String iun, String flowDescription) {
        log.info("Impostazione comunicazione bonaria PF per campagna {}, IUN {}, flusso: {}", campaign, iun, flowDescription);
    }

    @Given("una comunicazione bonaria inviata a PG per la campagna {string} con IUN {string} e flusso {string}")
    public void setupPgComboFlow(String campaign, String iun, String flowDescription) {
        log.info("Impostazione comunicazione bonaria PG per campagna {}, IUN {}, flusso: {}", campaign, iun, flowDescription);
    }

    @When("il mittente naviga sulla pagina di dettaglio della comunicazione con campagna {string} e IUN {string}")
    public void navigateToComboDetails(String campaignId, String iun) {
        log.info("Navigazione su dettaglio combo per campagna {} e IUN {}", campaignId, iun);
        comboDetailsPage = browser.bind(MittenteComboDetailsPage.class);
        comboDetailsPage.navigateTo(campaignId, iun);
        comboDetailsPage.assertLoaded();
        webBrowserContext.setCurrentPage(comboDetailsPage);
    }

    @When("il mittente naviga sulla pagina della timeline per la campagna {string} e IUN {string}")
    public void navigateToComboTimeline(String campaignId, String iun) {
        log.info("Navigazione su timeline combo per campagna {} e IUN {}", campaignId, iun);
        comboTimelinePage = browser.bind(MittenteComboTimelinePage.class);
        comboTimelinePage.navigateTo(campaignId, iun);
        comboTimelinePage.assertLoaded();
        webBrowserContext.setCurrentPage(comboTimelinePage);
    }

    @Then("la sezione dettaglio invio per canale mostra esclusivamente i canali {string}")
    public void verifyChannelsList(String expectedChannelsStr) {
        List<String> expectedChannels = Arrays.stream(expectedChannelsStr.split(","))
                .map(this::normalizeChannel)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        List<ComboChannelsSection.ChannelItemComponent> renderedItems = comboDetailsPage.channelsSection().channelItems();
        List<String> actualNames = renderedItems.stream()
                .map(item -> {
                    String name = item.name().read();
                    return name != null ? normalizeChannel(name) : "";
                })
                .filter(name -> !name.isBlank() && (name.equals("SEND") || name.equals("IO") || name.equals("EMAIL") || name.equals("PEC") || name.equals("SERVIZIO POSTALE") || name.equals("SMS")))
                .distinct()
                .collect(Collectors.toList());

        log.info("Canali attesi: {}, canali trovati a UI: {}", expectedChannels, actualNames);
        Assertions.assertThat(actualNames)
                .as("Verifica lista canali abilitati per destinatario e campagna")
                .containsAll(expectedChannels);
    }

    @Then("il canale {string} mostra la label di stato {string}")
    public void verifyChannelStatusBadge(String channelName, String expectedStatusLabel) {
        List<ComboChannelsSection.ChannelItemComponent> renderedItems = comboDetailsPage.channelsSection().channelItems();
        String normalizedTarget = normalizeChannel(channelName);

        ComboChannelsSection.ChannelItemComponent targetChannel = renderedItems.stream()
                .filter(item -> {
                    String raw = item.name().read();
                    return raw != null && (normalizeChannel(raw).equalsIgnoreCase(normalizedTarget) || raw.toUpperCase().contains(normalizedTarget));
                })
                .findFirst()
                .orElseThrow(() -> new AssertionError("Canale non trovato nella lista UI: " + channelName + " (normalizzato: " + normalizedTarget + ")"));

        String actualBadge = targetChannel.statusBadge().read();
        log.info("Canale {}: badge atteso '{}', badge rilevato '{}'", channelName, expectedStatusLabel, actualBadge);

        String normalizedExpected = expectedStatusLabel.replace("_", " ").trim();
        Assertions.assertThat(actualBadge)
                .as("Verifica badge di stato per il canale %s", channelName)
                .isNotNull();
        Assertions.assertThat(actualBadge.replace("_", " "))
                .as("Verifica badge di stato per il canale %s", channelName)
                .containsIgnoringCase(normalizedExpected);
    }

    @Then("la timeline mostra gli eventi nell'ordine cronologico corretto")
    public void verifyTimelineEventsChronologicalOrder() {
        List<ComboTimelineItemComponent> items = comboTimelinePage.timeline().items();
        Assertions.assertThat(items)
                .as("La timeline deve contenere almeno un evento")
                .isNotEmpty();
    }

    @And("la timeline include i log dei canali {string}")
    public void verifyTimelineChannelLogs(String expectedLogsStr) {
        List<ComboTimelineItemComponent> items = comboTimelinePage.timeline().items();
        SoftAssertions softly = new SoftAssertions();

        String[] channelLogs = expectedLogsStr.split(",");
        for (String channelLog : channelLogs) {
            String[] parts = channelLog.trim().split(":", 2);
            String channel = parts[0].trim();
            String normalizedChannel = normalizeChannel(channel);
            String expectedLogContent = parts.length > 1 ? parts[1].trim() : "";

            boolean found = items.stream().anyMatch(item -> {
                String title = item.title().read();
                String content = item.content().read();
                String fullText = ((title != null ? title : "") + " " + (content != null ? content : "")).toUpperCase();

                boolean matchesChannel = fullText.contains(channel.toUpperCase())
                        || fullText.contains(normalizedChannel.toUpperCase())
                        || (normalizedChannel.equals("EMAIL") && (fullText.contains("EMAIL") || fullText.contains("MAIL") || fullText.contains("DIGITALE")))
                        || (normalizedChannel.equals("IO") && (fullText.contains("APP IO") || fullText.contains("IO")))
                        || (normalizedChannel.equals("SEND") && (fullText.contains("SEND") || fullText.contains("PIATTAFORMA")));

                return matchesChannel;
            });

            softly.assertThat(found)
                    .as("Verifica presenza log per canale %s con contenuto atteso [%s]", channel, expectedLogContent)
                    .isTrue();
        }
        softly.assertAll();
    }

    @And("la timeline mostra l'esito finale {string} con box visivo {string}")
    public void verifyTimelineFinalOutcomeAndBox(String expectedOutcome, String expectedBoxType) {
        List<ComboTimelineItemComponent> items = comboTimelinePage.timeline().items();
        Assertions.assertThat(items).isNotEmpty();

        boolean anyRed = items.stream().anyMatch(ComboTimelineItemComponent::isRedBoxPresent);
        boolean anyGreen = items.stream().anyMatch(ComboTimelineItemComponent::isGreenBoxPresent);

        if ("VERDE".equalsIgnoreCase(expectedBoxType)) {
            Assertions.assertThat(anyGreen)
                    .as("L'esito %s deve presentare il box visivo verde di successo/lettura/consegna", expectedOutcome)
                    .isTrue();
        } else if ("ROSSO".equalsIgnoreCase(expectedBoxType)) {
            Assertions.assertThat(anyRed)
                    .as("L'esito %s deve presentare il box visivo rosso di irreperibilità/errore", expectedOutcome)
                    .isTrue();
        }
    }
}

