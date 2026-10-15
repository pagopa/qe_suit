package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.APIKeyPage;
import it.pagopa.send.web.mittente.infrastructure.page.NewAPIKeyPage;
import it.pagopa.send.web.mittente.infrastructure.page.PlatformStatusPage;
import it.pagopa.send.web.mittente.infrastructure.page.StatisticsPage;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
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

/**
 * Sostituisce gli scenari "verifica la raggiungibilità delle pagine" di
 * {@code features/mittenti/navigazione-send-mittenti.feature}: per ciascuna pagina del portale
 * mittenti, la navigazione diretta (via {@link WebBrowserContractValidator#as}) e il conseguente
 * {@code assertLoaded()} sono già garantiti dal framework di contract test (si veda
 * {@code WebContractRuntimeCaseExecutor}), quindi ogni scenario qui sotto è un no-op: il test
 * fallisce se e solo se la pagina non è raggiungibile o non si carica correttamente.
 */
@ActiveProfiles({"test", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebMittenteNavigationContractTest {

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldReachDashboard() {
        return reachabilityTest(DashboardPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachCreateNotification() {
        return reachabilityTest(CreateNotificationPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachApiKey() {
        return reachabilityTest(APIKeyPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachNewApiKey() {
        return reachabilityTest(NewAPIKeyPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachStatistics() {
        return reachabilityTest(StatisticsPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachPlatformStatus() {
        return reachabilityTest(PlatformStatusPage.class);
    }

    private <P extends Page> Stream<DynamicTest> reachabilityTest(Class<P> pageType) {
        return webContractValidator.as(Tenant.GROSSINI, List.of())
                .on(pageType)
                .tests(Stream.of(new WebScenario<>(
                        "controllo caricamento pagina " + pageType.getSimpleName(),
                        page -> {},
                        page -> {}
                )));
    }
}
