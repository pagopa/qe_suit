package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.ResourceLock;
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
public class WebEServiceDuplicationContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldDuplicateEServiceWithActiveProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;
        EService eService = createPublishedEService(delegator);

        interopJourney.createActiveProducerDelegation(delegator, delegate, eService.getRef());

        return duplicationContract(delegator, eService, "ACTIVE");
    }

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldDuplicateEServiceWithWaitingForApprovalProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;
        EService eService = createPublishedEService(delegator);

        interopJourney.createWaitingForApprovalProducerDelegation(delegator, delegate, eService.getRef());

        return duplicationContract(delegator, eService, "WAITING_FOR_APPROVAL");
    }

    private EService createPublishedEService(Tenant producer) {
        return interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
    }

    private Stream<DynamicTest> duplicationContract(Tenant delegator, EService eService, String delegationState) {
        return webContractValidator
                .as(User.getTenantAdmin(delegator), delegator)
                .on(
                        ProducerEServiceDetailPage.class,
                        eService.getId().toString(),
                        eService.getActiveDescriptor().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "Duplica e-service disponibile al delegante ADMIN con delega " + delegationState
                                + ": navigazione alla modifica della nuova bozza",
                        page -> {
                            page.pageTitle().readAndAssert(eService.getName());
                            page.duplicateEService();
                        },
                        page -> page.duplicatedEServicePage().assertLoaded()
                )));
    }
}
