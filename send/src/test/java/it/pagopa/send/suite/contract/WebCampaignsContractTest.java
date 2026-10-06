package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Readable;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
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
public class WebCampaignsContractTest {
    private final WebBrowserContractValidator webContractValidator;
    @Value("${token.mittente}")
    private String veronaSelfCareToken;

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
        String selfCareToken = "";
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignsPage.class,selfCareToken)
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
                .on(CampaignsPage.class,veronaSelfCareToken)
                .tests(campagneListaPopolataScenarios());
    }

    private static @NonNull Stream<WebScenario<CampaignsPage>> campagneListaPopolataScenarios() {
        return Stream.of(new WebScenario<>(
                "Lista di campagne popolata",
                page -> {
                },
                page -> {
                    Assertions.assertThat(page.box().elements()).as("La lista è vuota ma dovrebbe essere popolata").isNotEmpty();
                }
        ));
    }

    @TestFactory
    Stream<DynamicTest> campagneElementiListaCorretti() {
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignsPage.class,veronaSelfCareToken)
                .tests(campagneElementiListaCorrettiScenarios());
    }

    private static @NonNull Stream<WebScenario<CampaignsPage>> campagneElementiListaCorrettiScenarios() {
        return Stream.of(new WebScenario<>(
                "Elementi della lista corretti",
                page -> {
                },
                page -> {
                    for (CampagneElement elem : page.box().elements()){
                        Assertions.assertThat(elem.apriCampagna().isDisabled()).as("Il bottone è disabilitato").isFalse();
                        Assertions.assertThat(elem.fields().size()).as("Non sono presenti le 4 labels previste").isEqualTo(4);
                        boolean contieneCodiceId = elem.fields().stream()
                                .map(Readable::read).anyMatch("Codice ID"::equals);
                        boolean contieneData =  elem.fields().stream()
                                .map(Readable::read).anyMatch(s -> s.matches("\\d{2}/\\d{2}/\\d{4}"));
                        Assertions.assertThat(contieneCodiceId).as("Non è presente il campo 'Codice ID'").isTrue();
                        Assertions.assertThat(contieneData).as("Non è presente la data").isTrue();
                    }
                }
        ));
    }@TestFactory
    Stream<DynamicTest> campagneElementiPaginazione() {
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignsPage.class,veronaSelfCareToken)
                .tests(campagneElementiPaginazioneScenarios());
    }

    private static @NonNull Stream<WebScenario<CampaignsPage>> campagneElementiPaginazioneScenarios() {
        return Stream.of(new WebScenario<>(
                "Verifica paginazione coerente",
                page -> {
                },
                page -> {
                        Assertions.assertThat(page.box().elements().size())
                                .as("Gli elementi della pagina sono maggiori di quelli richiesti dalla paginazione")
                                .isLessThanOrEqualTo(Integer.parseInt(page.rowsXPageButton().read()));
                }
        ));
    }
}
