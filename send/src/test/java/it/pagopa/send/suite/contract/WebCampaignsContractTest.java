package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.campaigns.application.CampaignsGateway;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.CampaignSummary;
import it.pagopa.send.web.campaigns.infrastructure.page.CampaignDetailPage;
import it.pagopa.send.web.campaigns.infrastructure.page.CampaignsPage;
import it.pagopa.send.web.campaigns.infrastructure.page.component.CampagneElement;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

@ActiveProfiles({"dev", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebCampaignsContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final CampaignsGateway campaignsGateway;

    @TestFactory
    Stream<DynamicTest> campagneButtonIsPresentIntoSidebar() {
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(DashboardPage.class)
                .tests(campagneButtonIsPresentIntoSidebarScenarios());
    }

    private static @NonNull Stream<WebScenario<DashboardPage>> campagneButtonIsPresentIntoSidebarScenarios() {
        return Stream.of(new WebScenario<>(
                "La voce della sidebar 'Campagne' è presente e cliccabile",
                page -> {
                    page.sidebar().comunicaConSend().click();
                    page.sidebar().campagne().click();
                },
                page -> {
                    page.sidebar().campagne().assertLoaded();
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> campagneEmptyState() {
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignsPage.class)
                .tests(campagneEmptyStateScenarios());
    }

    private static @NonNull Stream<WebScenario<CampaignsPage>> campagneEmptyStateScenarios() {
        return Stream.of(new WebScenario<>(
                "Lista di campagne non popolata",
                page -> {
                },
                page -> {
                    Assertions.assertThat(page.emptyStateLabel().read()).isNotNull();
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> campagneListaPopolata() {
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignsPage.class)
                .tests(campagneListaPopolataScenarios());
    }

    private static @NonNull Stream<WebScenario<CampaignsPage>> campagneListaPopolataScenarios() {
        return Stream.of(new WebScenario<>(
                "Lista di campagne popolata",
                page -> {
                },
                page -> {
                    Assertions.assertThat(page.table().elements()).as("La lista è vuota ma dovrebbe essere popolata").isNotEmpty();
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> campagneElementiListaCorretti() {
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignsPage.class)
                .tests(campagneElementiListaCorrettiScenarios());
    }

    private static @NonNull Stream<WebScenario<CampaignsPage>> campagneElementiListaCorrettiScenarios() {
        return Stream.of(new WebScenario<>(
                "Elementi della lista corretti",
                page -> {
                },
                page -> {
                    for (CampagneElement elem : page.table().elements()){
                        Assertions.assertThat(elem.apriCampagna().isDisabled()).as("Il bottone è disabilitato").isFalse();
                        Assertions.assertThat(elem.fields().size()).as("Non sono presenti le 4 labels previste").isEqualTo(4);
                    }
                }
        ));
    }



    @TestFactory
    Stream<DynamicTest> dettaglioCampagna() {
        List<CampaignSummary> results = campaignsGateway.getCampaigns();
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignDetailPage.class,results.get(0).getCampaignId())
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
        List<CampaignSummary> results = campaignsGateway.getCampaigns();
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignDetailPage.class,results.get(0).getCampaignId())
                .tests(dettaglioCampagnaEmptyStateScenarios(results.get(0)));
    }

    private static @NonNull Stream<WebScenario<CampaignDetailPage>> dettaglioCampagnaEmptyStateScenarios(CampaignSummary summary) {
        return Stream.of(new WebScenario<>(
                "Verifica pagina dettaglio notifica con nessuna comunicazione",
                page -> {},
                page -> {
                    Assertions.assertThat(page.emptyStateLabel().read()).as("Sono presenti comunicazioni").isEqualTo("Nessuna comunicazione disponibile");
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> dettaglioCampagnaRicerca() {
        List<CampaignSummary> results = campaignsGateway.getCampaigns();
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignDetailPage.class,results.get(1).getCampaignId())
                .tests(dettaglioCampagnaRicercaScenarios(results.get(1)));
    }

    private static @NonNull Stream<WebScenario<CampaignDetailPage>> dettaglioCampagnaRicercaScenarios(CampaignSummary summary) {
        return Stream.of(new WebScenario<>(
                "Verifica pagina dettaglio notifica con ricerca",
                page -> {
                    page.recipientId().fill("DRCGNN12A46A326K");
                    page.filterButton().click();
                },
                page -> {
                    Assertions.assertThat(page.communications().rows().size()).as("Le comunicazioni trovate sono diverse da 1").isEqualTo(2);
                    Assertions.assertThat(page.communications()
                            .rows()
                            .get(0)
                            .cells()
                            .get(0)
                            .value()
                            .read()).as("Valore campo recipientId non congruo").isEqualTo("DRCGNN12A46A326K");
                }
        ));
    }
}
