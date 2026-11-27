package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainUseCase;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychain;
import it.pagopa.interop.common.producer_keychain.domain.ProducerKeychainRef;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerKeychainListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"channel.web.browser=chrome"}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerKeychainListPageContractTest {

    private static final Tenant TENANT = Tenant.COMUNE_DI_MILANO;
    private static final String EXPECTED_TITLE = "I miei portachiavi erogatore";
    private static final String EXPECTED_DESCRIPTION =
            "In quest’area puoi visualizzare e gestire i portachiavi associati ai tuoi e-service, caricando le chiavi pubbliche necessarie per la verifica delle risposte da parte dei fruitori.";
    private static final String EXPECTED_CREATE_BUTTON = "Crea nuovo";
    private static final String EXPECTED_NAME_FILTER_LABEL = "Cerca per nome";
    private static final String EXPECTED_KEYCHAIN_COLUMN_HEADER = "Portachiavi";
    private static final String EXPECTED_INSPECT_ACTION = "Visualizza";
    private static final String EXPECTED_DELETE_ACTION = "Elimina";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final ProducerKeychainFactory producerKeychainFactory;
    private final ProducerKeychainUseCase producerKeychainUseCase;
    private final AuthSession authSession;

    private final Queue<ProducerKeychainRef> createdKeychains = new ConcurrentLinkedQueue<>();

    @AfterAll
    void cleanUpCreatedKeychains() {
        List<Throwable> cleanupFailures = new ArrayList<>();
        ProducerKeychainRef ref;
        while ((ref = createdKeychains.poll()) != null) {
            try (AuthSession.Snapshot ignored = authSession.snapshot()) {
                interopJourney
                        .switchChannel(Channel.BFF)
                        .withProducer(TENANT, UserRole.ADMIN);
                producerKeychainUseCase.deleteProducerKeychain(ref);
            } catch (Throwable throwable) {
                cleanupFailures.add(throwable);
            }
        }
        if (!cleanupFailures.isEmpty()) {
            RuntimeException aggregated = new RuntimeException("Failed to clean up created producer keychain(s)");
            cleanupFailures.forEach(aggregated::addSuppressed);
            throw aggregated;
        }
    }

    /**
     * Verifies that the producer keychain list page shows the expected breadcrumbs, title,
     * description, primary action, filter, table header, pagination controls and the producer
     * keychain created as precondition for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> producerKeychainListPageContract() {
        User user = User.getTenantAdmin(TENANT);
        ProducerKeychain createdKeychain = createPreconditionKeychain(user);

        return webContractValidator
                .as(user, TENANT)
                .on(ProducerKeychainListPage.class)
                .tests(contractScenarios(createdKeychain));
    }

    private ProducerKeychain createPreconditionKeychain(User user) {
        try (AuthSession.Snapshot ignored = authSession.snapshot()) {
            interopJourney.switchChannel(Channel.BFF);
            ProducerKeychainCreationCommand command = producerKeychainFactory.creationCommand(
                    List.of(user.getUserId())
            );
            ProducerKeychain createdKeychain = interopJourney
                    .withProducer(TENANT, UserRole.ADMIN)
                    .createProducerKeychain(command)
                    .get(ProducerKeychain.class);
            createdKeychains.add(createdKeychain.getRef());
            return createdKeychain;
        }
    }

    private Stream<WebScenario<ProducerKeychainListPage>> contractScenarios(ProducerKeychain createdKeychain) {
        return Stream.of(
                new WebScenario<>(
                        "Producer keychain list page shows the expected content, filters, actions and created keychain",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            List<String> breadcrumbItems = page.breadcrumbs().items().stream()
                                    .map(Readable::read)
                                    .toList();
                            softly.assertThat(breadcrumbItems)
                                    .as("Breadcrumbs items")
                                    .isNotEmpty()
                                    .allSatisfy(item -> softly.assertThat(item).isNotBlank());

                            softly.assertThat(page.pageTitle().read())
                                    .as("Page title")
                                    .isEqualTo(EXPECTED_TITLE);

                            softly.assertThat(page.pageDescription().read())
                                    .as("Page description")
                                    .isEqualTo(EXPECTED_DESCRIPTION);

                            softly.assertThat(page.createNewButton().exists(Gettable.FindPolicy.PRESENT))
                                    .as("Create new button should be present")
                                    .isTrue();
                            softly.assertThat(page.createNewButton().exists(Gettable.FindPolicy.VISIBLE))
                                    .as("Create new button should be visible")
                                    .isTrue();
                            softly.assertThat(page.createNewButton().isDisabled())
                                    .as("Create new button should be enabled")
                                    .isFalse();
                            softly.assertThat(page.createNewButton().read())
                                    .as("Create new button label")
                                    .isEqualTo(EXPECTED_CREATE_BUTTON);

                            softly.assertThat(page.nameFilterLabel().read())
                                    .as("Name filter label")
                                    .isEqualTo(EXPECTED_NAME_FILTER_LABEL);
                            softly.assertThat(page.nameFilterInput().read())
                                    .as("Name filter initial value")
                                    .isEmpty();

                            softly.assertThat(page.keychainsTable().get().isPresent())
                                    .as("Producer keychains table should be present")
                                    .isTrue();
                            TableRow header = page.keychainsTable().tHead().rows().get(0);
                            softly.assertThat(header.cells().get(0).read())
                                    .as("Keychain column header")
                                    .isEqualTo(EXPECTED_KEYCHAIN_COLUMN_HEADER);
                            if (header.cells().size() > 1) {
                                softly.assertThat(
                                                header.cells().get(1).get(Gettable.FindPolicy.PRESENT)
                                                        .map(WebPresentationElement::getText)
                                                        .orElse("")
                                        )
                                        .as("Actions column header should be empty")
                                        .isEmpty();
                            }

                            List<Integer> sizeValues = page.rowsPerPageSelector().getSizeValues();
                            softly.assertThat(sizeValues.size())
                                    .as("Rows per page selector should have more than one option")
                                    .isGreaterThan(1);
                            int initialSize = page.rowsPerPageSelector().getSelectedSize();
                            softly.assertThat(initialSize)
                                    .as("Rows per page should be initialized to the lowest value")
                                    .isEqualTo(sizeValues.get(0));

                            int initialRowCount = page.keychainsTable().tBody().rows().size();
                            page.rowsPerPageSelector().setSize(sizeValues.get(1));
                            int updatedRowCount = PollingUtils.pollUntil(
                                    () -> page.keychainsTable().tBody().rows().size(),
                                    updated -> updated >= initialRowCount,
                                    Duration.ofSeconds(20),
                                    Duration.ofSeconds(1),
                                    "Records affected by new pagination size have not been rendered yet"
                            );
                            page.rowsPerPageSelector().setSize(initialSize);
                            if (updatedRowCount > initialRowCount) {
                                PollingUtils.pollUntil(
                                        () -> page.pagination().exists(),
                                        Boolean::booleanValue,
                                        Duration.ofSeconds(20),
                                        Duration.ofSeconds(1),
                                        "Pagination has not been rendered yet after restoring initial page size"
                                );
                                softly.assertThat(page.pagination().exists())
                                        .as("Pagination should be displayed when more rows than the first page are available")
                                        .isTrue();
                            } else {
                                softly.assertThat(page.pagination().exists())
                                        .as("Pagination should not be required when all %s rows fit into a single page of %s elements", updatedRowCount, initialRowCount)
                                        .isFalse();
                            }

                            Optional<ProducerKeychainListPage.KeychainRow> createdRowOpt =
                                    page.findKeychainRowByName(createdKeychain.getName());
                            softly.assertThat(createdRowOpt)
                                    .as("Created producer keychain '%s' should be present in table", createdKeychain.getName())
                                    .isPresent();

                            createdRowOpt.ifPresent(createdRow -> {
                                softly.assertThat(createdRow.inspectButton().exists(Gettable.FindPolicy.VISIBLE))
                                        .as("Inspect button should be visible for created keychain")
                                        .isTrue();
                                softly.assertThat(createdRow.inspectButton().isDisabled())
                                        .as("Inspect button should be enabled for created keychain")
                                        .isFalse();
                                softly.assertThat(createdRow.inspectButton().read())
                                        .as("Inspect button label")
                                        .isEqualTo(EXPECTED_INSPECT_ACTION);

                                softly.assertThat(createdRow.actionMenuButton().exists(Gettable.FindPolicy.VISIBLE))
                                        .as("Action menu button should be visible for created keychain")
                                        .isTrue();
                                softly.assertThat(createdRow.actionMenuButton().isDisabled())
                                        .as("Action menu button should be enabled for created keychain")
                                        .isFalse();

                                createdRow.actionMenuButton().click();
                                try {
                                    softly.assertThat(page.deleteMenuItem().get(Gettable.FindPolicy.VISIBLE))
                                            .as("Delete menu item should be visible when row action menu is opened")
                                            .isPresent();
                                    softly.assertThat(page.deleteMenuItem().read())
                                            .as("Delete menu item label")
                                            .isEqualTo(EXPECTED_DELETE_ACTION);
                                } finally {
                                    page.closeActionMenu();
                                }
                            });
                        })
                )
        );
    }
}

