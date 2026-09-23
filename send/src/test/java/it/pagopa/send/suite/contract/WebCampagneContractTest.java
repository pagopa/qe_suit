package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.web.campagne.infrastructure.page.CampaignDetailPage;
import it.pagopa.send.web.campagne.infrastructure.page.CampaignsPage;
import it.pagopa.send.web.campagne.infrastructure.page.component.CampagneElement;
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
public class WebCampagneContractTest {
    private final WebBrowserContractValidator webContractValidator;

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
        return webContractValidator
                .as(Tenant.GROSSINI, List.of())
                .on(CampaignDetailPage.class)
                .tests(dettaglioCampagnaScenarios());
    }

    private static @NonNull Stream<WebScenario<CampaignDetailPage>> dettaglioCampagnaScenarios() {
        return Stream.of(new WebScenario<>(
                "Verifica dettaglio notifica",
                page -> {
                    page.labels().forEach(x-> System.out.println(x.read()));
                },
                page -> {
                    Assertions.assertThat(page.labels().size()).as("Non sono presenti le 8 labels previste").isEqualTo(8);
                }
        ));
    }
}
