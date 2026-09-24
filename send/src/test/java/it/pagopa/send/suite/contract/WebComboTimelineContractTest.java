package it.pagopa.send.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.combo.MittenteComboTimelinePage;
import lombok.RequiredArgsConstructor;
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
public class WebComboTimelineContractTest {

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateComboTimelinePageLayout() {
        String dummyCampaignId = "FattOrd";
        String dummyIun = "GLAM-ZTPT-NZQG-202609-K-A";

        return webContractValidator.as(Tenant.GROSSINI, List.of(Recipient.LUCREZIA))
                .on(MittenteComboTimelinePage.class, dummyCampaignId, dummyIun)
                .tests(scenarios());
    }

    private Stream<WebScenario<MittenteComboTimelinePage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Controllo caricamento atomico componenti pagina Timeline Combo",
                        page -> {},
                        page -> page.assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo caricamento items timeline",
                        page -> {},
                        page -> page.timeline().assertLoaded()
                ),
                new WebScenario<>(
                        "Controllo visualizzazione responsive card eventi e gestione wrapping testi",
                        page -> {},
                        page -> {
                            page.timeline().items().forEach(item -> {
                                item.title().readAndAssert(org.junit.jupiter.api.Assertions::assertNotNull);
                                item.timestamp().readAndAssert(org.junit.jupiter.api.Assertions::assertNotNull);
                            });
                        }
                )
        );
    }
}

