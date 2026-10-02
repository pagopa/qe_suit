package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCatalogPage;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> shouldLoadProducerEServiceList() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(ProducerEServiceListPage.class)
                        .tests(Stream.of(new WebScenario<>(
                                "e-service erogati accessibili all'utente " + role.name() + " del Comune di Milano",
                                page -> {
                                },
                                page -> {
                                }
                        ))));
    }

    @TestFactory
    Stream<DynamicTest> shouldLoadEServiceCatalog() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(EServiceCatalogPage.class)
                        .tests(Stream.of(new WebScenario<>(
                                "catalogo e-service accessibile all'utente " + role.name() + " del Comune di Milano",
                                page -> {
                                },
                                page -> {
                                }
                        ))));
    }

    @TestFactory
    Stream<DynamicTest> shouldShowEServiceDetailToProducer() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        EService eService = interopJourney
                .withProducer(tenant, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);

        return Stream.of(UserRole.ADMIN, UserRole.API)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(
                                ProducerEServiceDetailPage.class,
                                eService.getId().toString(),
                                eService.getActiveDescriptor().getId().toString()
                        )
                        .tests(Stream.of(new WebScenario<>(
                                "nome dell'e-service creato visibile all'utente " + role.name() + " del Comune di Milano",
                                page -> {
                                },
                                page -> page.pageTitle().readAndAssert(eService.getName())
                        ))));
    }
}
