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
    private final String personalSelfCareToken="eyJraWQiOiJqd3QtZXhjaGFuZ2VfZWE6NDg6NTI6ZTQ6YWU6OGY6MzA6YjU6YWQ6M2M6ZDI6MDU6NzQ6Nzk6Yzk6ZWYiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJmaXNjYWxfbnVtYmVyIjoiUlNTR1BQODBCMDJHMjczSCIsIm5hbWUiOiJnaXVzZXBwZSIsImZhbWlseV9uYW1lIjoicm9zc2luaSIsInVpZCI6ImU5ZTRhOWM3LTk1ODYtNGI5Mi1hN2RkLWVlMWEwZTc3ZDM5OCIsInRlbmFudF9pZCI6IkFSIiwic3BpZF9sZXZlbCI6Imh0dHBzOi8vd3d3LnNwaWQuZ292Lml0L1NwaWRMMiIsImlzcyI6Imh0dHBzOi8vdWF0LnNlbGZjYXJlLnBhZ29wYS5pdCIsImF1ZCI6InNlbGZjYXJlLnRlc3Qubm90aWZpY2hlZGlnaXRhbGkuaXQiLCJpYXQiOjE3OTE5ODQ2MDIsImV4cCI6MTc5MTk4NTUwMiwianRpIjoiMjQ5MTg3YTAtNTBlOS00ZjYwLThlYzctZWFiYjZlZDViNWE4IiwiZW1haWwiOiJncm9zc2luaUB0ZXN0LmNlcnQuaXQiLCJvcmdhbml6YXRpb24iOnsiaWQiOiI1Yjk5NGQ0YS0wZmE4LTQ3YWMtOWM3Yi0zNTRmMWQ0NGExY2UiLCJuYW1lIjoiQ29tdW5lIGRpIFBhbGVybW8iLCJyb2xlcyI6W3sicGFydHlSb2xlIjoiU1VCX0RFTEVHQVRFIiwicm9sZSI6ImFkbWluIn1dLCJmaXNjYWxfY29kZSI6IjgwMDE2MzUwODIxIiwiaXBhQ29kZSI6ImNfZzI3MyJ9LCJkZXNpcmVkX2V4cCI6MTc5MjAxMzE3NCwic3ViIjoiZTllNGE5YzctOTU4Ni00YjkyLWE3ZGQtZWUxYTBlNzdkMzk4IiwidHlwIjoiSUQifQ.JkfjNMIi0SYssADrP8QW0B023JCI1osHddH-7TxLvSKepeWIwPsU42I-als5iwo_rxA3WX3CkJioSkPlD_KvJP3edhwp5cPeZykiPsJcT5rSr44nkSQVYNqZjHOzlnnpCzyl9Z2yNCk01YFa_rjmWSvDhkUYWP4JoKZhtIkbii645ZX2LMp_q61xVEKdUmq-VNt8Mq3qSqXO5WAbb0a3Y-rlS8sXE9Vp1yO-xZoef5c5QPRtfh_m4edM_Bz6Z0hwxYVabMi3EVNWN0d_pc_pvqNvSfaIXdDg_HuDQ6jfIA_OXbzXL9bKgewfRryLM0PR49_KC_Ws7YxTBlAeSiu-hg&lang=it";

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
