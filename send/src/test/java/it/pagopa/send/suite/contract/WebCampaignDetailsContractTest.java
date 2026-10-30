package it.pagopa.send.suite.contract;

import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.campaigns.application.CampaignsUseCase;
import it.pagopa.send.common.informal_notification.domain.InformalRecipientSpec;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.CampaignSummary;
import it.pagopa.send.web.campaigns.infrastructure.page.CampaignDetailPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import it.pagopa.send.common.informal_notification.infrastructure.factory.InformalRecipientSpecFactory;

import java.util.Map;
import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Slf4j
public class WebCampaignDetailsContractTest {
    private final UserProvider userProvider;
    private final WebBrowserContractValidator webContractValidator;
    private final CampaignsUseCase campaignsUseCase;
    private final SendJourney sendJourney;
    private final InformalRecipientSpecFactory informalRecipientSpecFactory;

    @TestFactory
    Stream<DynamicTest> shouldShowCampaignDetail() {
        CampaignSummary summary = campaignsUseCase.getCampaigns().get(0);
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(CampaignDetailPage.class, summary.getCampaignId())
                .tests(Stream.of(new WebScenario<>(
                        "Verifica pagina dettaglio campagna",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.labels().size()).as("Non sono presenti le 8 labels previste").isEqualTo(8);
                            Assertions.assertThat(page.header().read()).as("Il titolo non corrisponde a quello recuperato").isEqualTo(summary.getTitle());
                        }
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldShowCampaignDetailWithoutCommunications() {
        CampaignSummary summary = campaignsUseCase.getCampaignByID("CampCancelled");
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(CampaignDetailPage.class, summary.getCampaignId())
                .tests(Stream.of(new WebScenario<>(
                        "Verifica pagina dettaglio campagna con nessuna comunicazione",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.emptyStateLabel().read()).as("Sono presenti comunicazioni").isEqualTo("Qui vedrai le comunicazioni della campagna.");
                        }
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldFindCommunicationBySearch() {
        String campaignId="BonarieAllChannels";
        String iun = sendJourney.getInformalNotification(sendDefaultInformalNotification(campaignId),60).getIun();
        //questo step lo utilizzo per vedere se la campagna esiste o meno
        campaignId = campaignsUseCase.getCampaignByID(campaignId).getCampaignId();
        String recipientTaxId = userProvider.resolve(UserAlias.USER_1).getTaxId();
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(CampaignDetailPage.class,campaignId)
                .tests(Stream.of(new WebScenario<>(
                        "Verifica pagina dettaglio campagna con ricerca",
                        page -> {
                            page.iunSearchInput().fill(iun);
                            page.recipientId().fill(recipientTaxId);
                            page.filterButton().click();
                        },
                        page -> {
                            var rows = page.communications().tBody().rows();
                            Assertions.assertThat(rows.size()).as("Le comunicazioni trovate sono diverse da 1").isEqualTo(1);
                            Assertions.assertThat(rows.get(0).cells().get(0).read()).as("Valore campo recipientId non congruo").isEqualTo(recipientTaxId);
                            Assertions.assertThat(rows.get(0).cells().get(1).read()).as("Valore campo iun non congruo").isEqualTo(iun);
                        }
                )));
    }

    private String sendDefaultInformalNotification(String campaignId) {
        InformalRecipientSpec recipientSpec = informalRecipientSpecFactory.build(
                userProvider.resolve(UserAlias.USER_1),
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

        return sendJourney
                .withInformalSender(userProvider.resolve(UserAlias.COMUNE_1))
                .prepareInformalNotification(Map.of(
                        "subject", "Test notifica bonaria Cucumber",
                        "campaignId", campaignId
                ))
                .withInformalRecipient(recipientSpec)
                .sendInformalNotification(userProvider.resolve(UserAlias.COMUNE_1))
                .getIun();
    }
}
