package it.pagopa.interop.suite.contract;


import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.delegation.application.command.ProducerDelegationCreationCommand;
import it.pagopa.interop.common.delegation.domain.DelegationState;
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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Test di contratto relativi alla feature
 * <a href="https://pagopa.atlassian.net/browse/PIN-10841">PIN-10841</a>,
 * inizialmente prevista per la release 2.24.0 e posticipata alla 2.25.0.
 * In attesa del rilascio della feature, i test non sono funzionanti e sono esclusi
 * dagli NRT tramite {@code @Tag("ignore")}.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Tag("ignore")
public class WebEServiceDuplicationContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldDuplicateEServiceWithActiveProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createProducerDelegation(ProducerDelegationCreationCommand.builder()
                    .delegator(delegator)
                    .delegate(delegate)
                    .eServiceRef(eService.getRef())
                    .targetState(DelegationState.ACTIVE)
                    .build());

            return webContractValidator
                    .as(User.getTenantUser(delegator, role), delegator)
                    .on(
                            ProducerEServiceDetailPage.class,
                            eService.getId().toString(),
                            eService.getActiveDescriptor().getId().toString()
                    )
                    .tests(Stream.of(duplicationScenario(role, eService, "ACTIVE")));
        });
    }

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldDuplicateEServiceWithWaitingForApprovalProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createProducerDelegation(ProducerDelegationCreationCommand.builder()
                    .delegator(delegator)
                    .delegate(delegate)
                    .eServiceRef(eService.getRef())
                    .targetState(DelegationState.WAITING_FOR_APPROVAL)
                    .build());

            return webContractValidator
                    .as(User.getTenantUser(delegator, role), delegator)
                    .on(
                            ProducerEServiceDetailPage.class,
                            eService.getId().toString(),
                            eService.getActiveDescriptor().getId().toString()
                    )
                    .tests(Stream.of(duplicationScenario(role, eService, "WAITING_FOR_APPROVAL")));
        });
    }

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldHideDuplicationFromDelegateWithActiveProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createProducerDelegation(ProducerDelegationCreationCommand.builder()
                    .delegator(delegator)
                    .delegate(delegate)
                    .eServiceRef(eService.getRef())
                    .targetState(DelegationState.ACTIVE)
                    .build());

            return webContractValidator
                    .as(User.getTenantUser(delegate, role), delegate)
                    .on(
                            ProducerEServiceDetailPage.class,
                            eService.getId().toString(),
                            eService.getActiveDescriptor().getId().toString()
                    )
                    .tests(Stream.of(unavailableDuplicationScenario(role, eService, "ACTIVE")));
        });
    }

    @TestFactory
    @ResourceLock("producer-delegation-availability:COMUNE_DI_POZZALLO")
    Stream<DynamicTest> shouldHideDuplicationFromDelegateWithWaitingForApprovalProducerDelegation() {
        Tenant delegator = Tenant.COMUNE_DI_MILANO;
        Tenant delegate = Tenant.COMUNE_DI_POZZALLO;

        return Stream.of(UserRole.ADMIN, UserRole.API, UserRole.API_SECURITY).flatMap(role -> {
            EService eService = createPublishedEService(delegator);
            interopJourney.createProducerDelegation(ProducerDelegationCreationCommand.builder()
                    .delegator(delegator)
                    .delegate(delegate)
                    .eServiceRef(eService.getRef())
                    .targetState(DelegationState.WAITING_FOR_APPROVAL)
                    .build());

            return webContractValidator
                    .as(User.getTenantUser(delegate, role), delegate)
                    .on(
                            ProducerEServiceDetailPage.class,
                            eService.getId().toString(),
                            eService.getActiveDescriptor().getId().toString()
                    )
                    .tests(Stream.of(unavailableDuplicationScenario(role, eService, "WAITING_FOR_APPROVAL")));
        });
    }

    private EService createPublishedEService(Tenant producer) {
        return interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
    }

    private WebScenario<ProducerEServiceDetailPage> duplicationScenario(
            UserRole role, EService eService, String delegationState) {
        return new WebScenario<>(
                "Duplica e-service disponibile al delegante " + role.name() + " con delega " + delegationState
                        + ": navigazione alla modifica della nuova bozza",
                page -> {
                    page.pageTitle().readAndAssert(eService.getName());
                    page.duplicateEService();
                },
                page -> page.duplicatedEServicePage().assertLoaded()
        );
    }

    private WebScenario<ProducerEServiceDetailPage> unavailableDuplicationScenario(
            UserRole role, EService eService, String delegationState) {
        return new WebScenario<>(
                "Duplica e-service assente per il delegato " + role.name() + " con delega " + delegationState,
                page -> {
                    page.pageTitle().readAndAssert(eService.getName());
                    page.actionsMenuButton().click();
                },
                this::assertDuplicationIsAbsentFromOpenMenu
        );
    }

    private void assertDuplicationIsAbsentFromOpenMenu(ProducerEServiceDetailPage page) {
        Assertions.assertThat(page.actionsMenu().get(Gettable.FindPolicy.VISIBLE))
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
