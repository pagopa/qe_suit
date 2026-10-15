package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
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
import it.pagopa.interop.web.eservice.infrastructure.WebEServiceGateway;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
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
    private final WebEServiceGateway webEServiceGateway;

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldDuplicateEServiceWithActiveProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createActiveProducerDelegation(delegator, delegate, eService.getRef());

            return duplicationContract(delegator, role, eService, "ACTIVE");
        });
    }

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldDuplicateEServiceWithWaitingForApprovalProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createWaitingForApprovalProducerDelegation(delegator, delegate, eService.getRef());

            return duplicationContract(delegator, role, eService, "WAITING_FOR_APPROVAL");
        });
    }

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldHideDuplicationFromDelegateWithActiveProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createActiveProducerDelegation(delegator, delegate, eService.getRef());

            return unavailableDuplicationContract(delegate, role, eService, "ACTIVE");
        });
    }

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldHideDuplicationFromDelegateWithWaitingForApprovalProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createWaitingForApprovalProducerDelegation(delegator, delegate, eService.getRef());

            return unavailableDuplicationContract(delegate, role, eService, "WAITING_FOR_APPROVAL");
        });
    }

    private EService createPublishedEService(Tenant producer) {
        return interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
    }

    private Stream<DynamicTest> duplicationContract(
            Tenant delegator, UserRole role, EService eService, String delegationState) {
        return webContractValidator
                .as(User.getTenantUser(delegator, role), delegator)
                .on(
                        ProducerEServiceDetailPage.class,
                        eService.getId().toString(),
                        eService.getActiveDescriptor().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "Duplica e-service disponibile al delegante " + role.name() + " con delega " + delegationState
                                + ": navigazione alla modifica della nuova bozza",
                        page -> {
                            page.pageTitle().readAndAssert(eService.getName());
                            webEServiceGateway.duplicateEService(page);
                        },
                        page -> page.duplicatedEServicePage().assertLoaded()
                )));
    }

    private Stream<DynamicTest> unavailableDuplicationContract(
            Tenant delegate, UserRole role, EService eService, String delegationState) {
        return webContractValidator
                .as(User.getTenantUser(delegate, role), delegate)
                .on(
                        ProducerEServiceDetailPage.class,
                        eService.getId().toString(),
                        eService.getActiveDescriptor().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "Duplica e-service assente per il delegato " + role.name() + " con delega " + delegationState,
                        page -> {
                            page.pageTitle().readAndAssert(eService.getName());
                            page.getActionsMenuButton().click();
                        },
                        this::assertDuplicationIsAbsentFromOpenMenu
                )));
    }

    private void assertDuplicationIsAbsentFromOpenMenu(ProducerEServiceDetailPage page) {
        Assertions.assertThat(page.actionsMenu().get(FindPolicy.VISIBLE))
                .as("Actions menu is open and visible")
                .isPresent();
        Assertions.assertThat(page.actionsMenuItems().getAll())
                .as("Actions menu items can be read")
                .hasValueSatisfying(items -> Assertions.assertThat(items)
                        .extracting(item -> item.getText().replaceAll("\\s+", " ").strip())
                        .as("Duplica e-service is absent from the delegate's actions menu")
                        .isNotEmpty()
                        .allSatisfy(label -> Assertions.assertThat(label).isNotBlank())
                        .doesNotContain("Duplica e-service"));
    }
}
