package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.infrastructure.page.component.TableCell;
import it.pagopa.send.web.mittente.infrastructure.page.APIKeyPage;
import it.pagopa.send.web.mittente.infrastructure.page.NewAPIKeyPage;
import it.pagopa.send.web.mittente.infrastructure.page.PlatformStatusPage;
import it.pagopa.send.web.mittente.infrastructure.page.StatisticsPage;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sostituisce gli scenari "verifica la raggiungibilità delle pagine" di
 * {@code features/mittenti/navigazione-send-mittenti.feature}: per ciascuna pagina del portale
 * mittenti, la navigazione diretta (via {@link WebBrowserContractValidator#as}) e il conseguente
 * {@code assertLoaded()} sono già garantiti dal framework di contract test (si veda
 * {@code WebContractRuntimeCaseExecutor}), quindi ogni scenario qui sotto è un no-op: il test
 * fallisce se e solo se la pagina non è raggiungibile o non si carica correttamente.
 */
@ActiveProfiles({"hotfix", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Slf4j
public class WebMittenteNavigationContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final String token = "";

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

    /**
     * Verifica la presenza della colonna "Gruppi" nella tabella delle notifiche
     *
     * <p>Il test recupera le celle dell'intestazione della tabella, ne registra
     * i valori nei log e verifica che almeno una di esse contenga esattamente
     * la dicitura "Gruppi".</p>
     *
     * <p>Il test fallisce se la colonna "Gruppi" non è presente tra le
     * intestazioni della tabella.</p>
     *
     * @return uno {@link Stream} di {@link DynamicTest} contenente
     *         lo scenario di verifica della colonna "Gruppi"
     */
    @TestFactory
    Stream<DynamicTest> checkColumnGroups() {
        return checkColumnGroupsScenarios();
    }

    private Stream<DynamicTest> checkColumnGroupsScenarios() {
        return webContractValidator.as(Tenant.GROSSINI, List.of())
                .on(DashboardPage.class,token)
                .tests(Stream.of(new WebScenario<>(
                        "Check presenza colonna 'Gruppi' su Dashboard Notifiche",
                        page -> {},
                        page ->
                        {
                            log.info("Controllo presenza della colonna...");
                            boolean checkGroupsPresence = false;
                            List<TableCell> headerCells =page
                                    .notifications()
                                    .header()
                                    .cells();

                            log.info("Headers presenti in tabella: {}", headerCells.stream().map(x -> x.read()).collect(Collectors.joining(", ")));
                            for (TableCell elem : headerCells){
                                if(elem.read().equals("Gruppi")) {
                                    log.info("Trovato elemento nella lista!");
                                    checkGroupsPresence = true;
                                    break;
                                }
                            }

                            Assertions.assertTrue(checkGroupsPresence);
                        }
                )));
    }
}
