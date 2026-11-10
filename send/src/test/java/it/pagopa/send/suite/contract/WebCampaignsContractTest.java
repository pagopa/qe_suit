package it.pagopa.send.suite.contract;

import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.common.user.domain.UserAlias;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.campaigns.application.CampaignsGateway;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.CampaignSummary;
import it.pagopa.send.web.campaigns.infrastructure.page.CampaignDetailPage;
import it.pagopa.send.web.campaigns.infrastructure.page.CampaignsPage;
import it.pagopa.send.web.campaigns.infrastructure.page.component.CampagneElement;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebCampaignsContractTest {
    private final UserProvider userProvider;
    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldShowCampaignsItemInSidebar() {
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(DashboardPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "La voce della sidebar 'Campagne' è presente e cliccabile",
                        page -> {
                            page.sidebar().comunicaConSend().click();
                            page.sidebar().campagne().click();
                        },
                        page -> {
                            page.sidebar().campagne().assertLoaded();
                        }
                )));
    }

    @Disabled("Serve un ente senza campagne: Comune_1 ne ha. Da riattivare quando ci sarà un'utenza adatta")
    @TestFactory
    Stream<DynamicTest> shouldShowEmptyCampaignsList() {
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(CampaignsPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "Lista di campagne non popolata",
                        page -> {
                        },
                        page -> {
                            Assertions.assertThat(page.emptyStateLabel().read()).isNotNull();
                        }
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldShowPopulatedCampaignsList() {
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(CampaignsPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "Lista di campagne popolata",
                        page -> {
                        },
                        page -> {
                            Assertions.assertThat(page.box().elements()).as("La lista è vuota ma dovrebbe essere popolata").isNotEmpty();
                        }
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldShowCampaignListItems() {
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(CampaignsPage.class)
                .tests(Stream.of(new WebScenario<>(
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
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldShowConsistentPagination() {
        return webContractValidator
                .as(userProvider.resolve(UserAlias.COMUNE_1))
                .on(CampaignsPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "Verifica paginazione coerente",
                        page -> {
                        },
                        page -> {
                            Assertions.assertThat(page.box().elements().size())
                                    .as("Gli elementi della pagina sono maggiori di quelli richiesti dalla paginazione")
                                    .isLessThanOrEqualTo(Integer.parseInt(page.rowsXPageButton().read()));
                        }
                )));
    }
}
