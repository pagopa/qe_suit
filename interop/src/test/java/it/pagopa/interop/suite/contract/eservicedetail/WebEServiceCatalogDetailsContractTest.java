package it.pagopa.interop.suite.contract.eservicedetail;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Contract of the catalog e-service detail page (see matrix.md).
 * Context [PUB]: Pozzallo/Admin session, e-service of Comune di Milano with a PUBLISHED descriptor.
 * Scenarios are read-only: initial state is verified before any interaction.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}//, "channel.web.headless=false"}
)
@Execution(ExecutionMode.SAME_THREAD)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceCatalogDetailsContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> catalogDetailsPageMustRespectContract() {
        EService eService = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
        EServiceDescriptor descriptor = eService.getActiveDescriptor();

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_POZZALLO), Tenant.COMUNE_DI_POZZALLO)
                .on(EServiceDetailPage.class, eService.getId().toString(), descriptor.getId().toString())
                .tests(scenarios(eService, descriptor));
    }

    private Stream<WebScenario<EServiceDetailPage>> scenarios(EService eService, EServiceDescriptor descriptor) {
        return Stream.of(
                CatalogDetailsHeaderScenarios.of(eService, descriptor),
                CatalogDetailsGeneralInfoScenarios.of(eService, descriptor),
                CatalogDetailsSectionsScenarios.of()
        ).flatMap(Function.identity());
    }
}

