package it.pagopa.interop.suite.integration;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposesListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Consumer Purposes List Web Integration Tests")
public class WebIntegrationConsumerPurposesListTest {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(1);

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    private final Tenant producer = Tenant.PAGO_PA;
    private final Tenant consumer = Tenant.SOGECAP;

    @TestFactory
    @DisplayName("Consumer purpose actions match the purpose state")
    Stream<DynamicTest> purposeActionsByState() {
        Purpose draftPurpose = createPurposeFixture(PurposeVersionState.DRAFT);
        Purpose waitingForApprovalPurpose = createPurposeFixture(PurposeVersionState.WAITING_FOR_APPROVAL);
        Purpose archivedPurpose = createPurposeFixture(PurposeVersionState.ARCHIVED);

        return webContractValidator
                .as(User.getTenantAdmin(consumer), consumer)
                .on(ConsumerPurposesListPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Purpose actions match draft, waiting for approval and archived states",
                                page -> {
                                },
                                page -> assertSoftly(softly -> {
                                    verifyMenuActions(page, draftPurpose, "in bozza", softly);
                                    verifyMenuActions(page, waitingForApprovalPurpose, "in attesa di approvazione", softly);
                                    verifyArchivedMenuActions(page, archivedPurpose, softly);
                                })
                        )
                ));
    }

    private Purpose createPurposeFixture(PurposeVersionState state) {
        return interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(
                        command -> command.name(RandomUtils.randomAlphanumericName("consumer-purpose-list")),
                        EServiceDescriptorState.PUBLISHED
                )
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(state)
                .get(Purpose.class);
    }

    private void verifyMenuActions(ConsumerPurposesListPage page, Purpose purpose, String expectedStatus, SoftAssertions softly) {
        ConsumerPurposesListPage.PurposeRow row = page.findPurposeRow(purpose.getTitle(), purpose.getId());
        assertThat(row.status().read())
                .as("Purpose status in column 4")
                .isEqualTo(expectedStatus);
        boolean buttonVisible = row.actionsButton().exists(FindPolicy.VISIBLE);
        softly.assertThat(buttonVisible)
                .as("Actions button in column 5 for purpose %s", purpose.getId())
                .isTrue();
        if (!buttonVisible) {
            return;
        }

        row.actionsButton().click();
        Throwable primaryFailure = null;
        try {
            awaitMenuVisibility(page, true);
            List<String> menuItems = page.actionsMenuItems().stream().map(Readable::read).toList();
            softly.assertThat(menuItems)
                    .as("Visible action menu items for purpose %s", purpose.getId())
                    .contains("Elimina")
                    .doesNotContain("Duplica");
        } catch (RuntimeException | Error failure) {
            primaryFailure = failure;
            throw failure;
        } finally {
            try {
                if (page.actionsMenu().exists(FindPolicy.VISIBLE)) {
                    page.actionsMenu().write(Keys.ESCAPE.toString());
                }
                awaitMenuVisibility(page, false);
            } catch (RuntimeException | Error cleanupFailure) {
                if (primaryFailure != null) {
                    primaryFailure.addSuppressed(cleanupFailure);
                } else {
                    throw cleanupFailure;
                }
            }
        }
    }

    private void verifyArchivedMenuActions(ConsumerPurposesListPage page, Purpose purpose, SoftAssertions softly) {
        ConsumerPurposesListPage.PurposeRow row = page.findPurposeRow(purpose.getTitle(), purpose.getId());
        assertThat(row.status().read())
                .as("Purpose status in column 4")
                .isEqualTo("archiviato");
        softly.assertThat(row.viewAction().exists(FindPolicy.VISIBLE))
                .as("Visualizza action should be visible in column 5 for archived purpose %s", purpose.getId())
                .isTrue();
        softly.assertThat(row.actionsButton().exists(FindPolicy.VISIBLE))
                .as("Actions button should not be visible for archived purpose %s", purpose.getId())
                .isFalse();
        softly.assertThat(page.actionsMenu().exists(FindPolicy.VISIBLE))
                .as("No residual purpose action menu should be visible")
                .isFalse();
        softly.assertThat(page.duplicateMenuItem().exists(FindPolicy.VISIBLE))
                .as("Duplicate must not be visible for an archived purpose")
                .isFalse();
    }

    private void awaitMenuVisibility(ConsumerPurposesListPage page, boolean expectedVisible) {
        PollingUtils.pollUntil(
                () -> page.actionsMenu().exists(FindPolicy.VISIBLE) == expectedVisible,
                visible -> visible,
                WAIT_TIMEOUT,
                POLL_INTERVAL,
                expectedVisible ? "Purpose action menu did not become visible" : "Purpose action menu did not close"
        );
    }
}
