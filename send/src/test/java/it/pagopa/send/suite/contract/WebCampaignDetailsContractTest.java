package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.campaigns.application.CampaignsGateway;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.CampaignSummary;
import it.pagopa.send.web.campaigns.infrastructure.page.CampaignDetailPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ActiveProfiles({"test", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebCampaignDetailsContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final CampaignsGateway campaignsGateway;
    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;
    @Value("${token.mittente}")
    private String veronaSelfCareToken;
    private final String personalSelfCareToken="";

    @TestFactory
    Stream<DynamicTest> dettaglioCampagna() {
        List<CampaignSummary> results = campaignsGateway.getCampaigns();
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignDetailPage.class,results.get(0).getCampaignId(),personalSelfCareToken)
                .tests(dettaglioCampagnaScenarios(results.get(0)));
    }

    private static @NonNull Stream<WebScenario<CampaignDetailPage>> dettaglioCampagnaScenarios(CampaignSummary summary) {
        return Stream.of(new WebScenario<>(
                "Verifica pagina dettaglio notifica",
                page -> {},
                page -> {
                    Assertions.assertThat(page.labels().size()).as("Non sono presenti le 8 labels previste").isEqualTo(8);
                    Assertions.assertThat(page.header().read()).as("Il titolo non corrisponde a quello recuperato").isEqualTo(summary.getTitle());

                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> dettaglioCampagnaEmptyState() {
        CampaignSummary result = campaignsGateway.getCampaignByID("CampCancelled");

        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignDetailPage.class,result.getCampaignId(),personalSelfCareToken)
                .tests(dettaglioCampagnaEmptyStateScenarios(result));
    }

    private static @NonNull Stream<WebScenario<CampaignDetailPage>> dettaglioCampagnaEmptyStateScenarios(CampaignSummary summary) {
        return Stream.of(new WebScenario<>(
                "Verifica pagina dettaglio notifica con nessuna comunicazione",
                page -> {},
                page -> {
                    Assertions.assertThat(page.emptyStateLabel().read()).as("Sono presenti comunicazioni").isEqualTo("Qui vedrai le comunicazioni della campagna.");
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> dettaglioCampagnaRicerca() {
        String campaignId="BonarieAllChannels";
        sendDefaultInformalNotification(campaignId);
        String iun = sendJourney.getLastInformalNotification().getIun();
        //questo step lo utilizzo per vedere se la campagna esiste o meno
        campaignId = campaignsGateway.getCampaignByID(campaignId).getCampaignId();
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignDetailPage.class,campaignId,personalSelfCareToken)
                .tests(dettaglioCampagnaRicercaScenarios(iun));
    }

    private static @NonNull Stream<WebScenario<CampaignDetailPage>> dettaglioCampagnaRicercaScenarios(String iun) {
        return Stream.of(new WebScenario<>(
                "Verifica pagina dettaglio notifica con ricerca",
                page -> {
                    page.iunSearchInput().fill(iun);
                    page.recipientId().fill(Recipient.LUCREZIA.getTaxId());
                    page.filterButton().click();
                },
                page -> {
                    Assertions.assertThat(page.communications().rows().size()).as("Le comunicazioni trovate sono diverse da 1").isEqualTo(2);
                    Assertions.assertThat(page.communications()
                            .rows()
                            .get(1)
                            .cells()
                            .get(0)
                            .value()
                            .read()).as("Valore campo recipientId non congruo").isEqualTo("BRGLRZ80D58H501Q");

                    Assertions.assertThat(page.communications()
                            .rows()
                            .get(1)
                            .cells()
                            .get(1)
                            .value()
                            .read()).as("Valore campo iun non congruo").isEqualTo(iun);
                }
        ));
    }

    private void sendDefaultInformalNotification(String campaignId) {
        InformalRecipientSpec recipientSpec = informalRecipientSpecFactory.build(
                Recipient.LUCREZIA,
                Map.of(
                        "email", "complaint@simulator.amazonses.com",
                        "phoneNumber", "+390000032181",
                        "physicalAddress_address", "Via @OK_RIS",
                        "physicalAddress_zip", "00133",
                        "physicalAddress_municipality", "Roma",
                        "physicalAddress_province", "RM",
                        "pagoPA_number", "1"
                )
        );

        sendJourney
                .withInformalSender(Tenant.GROSSINI)
                .prepareInformalNotification(Map.of(
                        "subject", "Test notifica bonaria Cucumber",
                        "campaignId", campaignId
                ))
                .withInformalRecipient(recipientSpec)
                .sendInformalNotification(Tenant.GROSSINI);
    }
}
