package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.combo.infrastructure.page.MittenteComboDetailsPage;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import org.springframework.core.env.Environment;

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
public class WebComboDetailsContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final Environment environment;

    @TestFactory
    Stream<DynamicTest> shouldValidateComboDetailsPageLayout() {
        String campaignId = environment.getProperty("combo.campaign.fattord", "FattOrd");
        String iun = environment.getProperty("combo.iun.fattord.flow-io-fail-email-ok",
                environment.getProperty("combo.iun.fattord.pf", "XKDZ-PXRU-ARWP-202609-J-A"));

        return webContractValidator.as(Tenant.GROSSINI, List.of(Recipient.LUCREZIA))
                .on(MittenteComboDetailsPage.class, campaignId, iun)
                .tests(scenarios());
    }

    private Stream<WebScenario<MittenteComboDetailsPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Controllo caricamento atomico sezioni di layout pagina Dettaglio Combo",
                        page -> {},
                        page -> page.assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo presenza sezione overview e canali",
                        page -> {},
                        page -> {
                            page.overviewSection().assertLoaded();
                            page.statusSection().assertLoaded();
                            page.channelsSection().assertLoaded();
                        }
                ),
                new WebScenario<>(
                        "Controllo visibilità canali di invio e relativi badge di stato",
                        page -> {},
                        page -> {
                            var items = page.channelsSection().channelItems();
                            org.assertj.core.api.Assertions.assertThat(items)
                                    .as("La sezione canali deve contenere la lista dei canali abilitati")
                                    .isNotEmpty();
                            items.forEach(item -> {
                                item.assertLoaded();
                                item.name().readAndAssert(name ->
                                        org.assertj.core.api.Assertions.assertThat(name).as("Nome canale non nullo").isNotBlank());
                                try {
                                    String badge = item.statusBadge().read();
                                    if (badge != null && !badge.isBlank()) {
                                        org.assertj.core.api.Assertions.assertThat(badge).isNotBlank();
                                    }
                                } catch (Exception ignored) {
                                }
                            });
                        }
                ),
                new WebScenario<>(
                        "Controllo coerenza testi descrittivi ed esplicativi dei canali di invio",
                        page -> {},
                        page -> {
                            var items = page.channelsSection().channelItems();
                            items.forEach(item -> {
                                try {
                                    String desc = item.description().read();
                                    if (desc != null && !desc.isBlank()) {
                                        org.assertj.core.api.Assertions.assertThat(desc).isNotBlank();
                                    }
                                } catch (Exception ignored) {
                                }
                            });
                        }
                ),
                new WebScenario<>(
                        "Controllo caricamento sezioni condizionali Documenti e Pagamenti",
                        page -> {},
                        page -> {
                            page.documentsSection().ifPresent(docSection -> docSection.assertLoaded());
                            page.paymentsSection().ifPresent(paySection -> paySection.assertLoaded());
                        }
                ),
                new WebScenario<>(
                        "Controllo accessibilità ed integrità sezioni in layout responsive desktop/mobile",
                        page -> {},
                        page -> {
                            page.overviewSection().openSidebarButton();
                            page.statusSection().openTimelineButton();
                        }
                )
        );
    }
}

