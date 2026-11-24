package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Menu;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.AgreementProducerPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAgreementProducerPageContractTest {

    private static final Tenant PRODUCER = Tenant.PAGO_PA;
    private static final Tenant CONSUMER = Tenant.COMUNE_DI_MILANO;

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the producer agreements list page shows the expected
     * copies, filters, table headers, created agreement row, actions and pagination controls.
     */
    @TestFactory
    Stream<DynamicTest> producerAgreementsListPageContract() {
        EService eservice = interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.DRAFT)
                .updateDescriptor(config->config
                        .agreementApprovalPolicy(AgreementApprovalPolicy.MANUAL),
                        EServiceDescriptorState.PUBLISHED)
                .get(EService.class);

        UUID eserviceId = eservice.getId();
        UUID descriptorId = eservice.getActiveDescriptor().getId();

        Agreement agreement = interopJourney
                .withConsumer(CONSUMER, UserRole.ADMIN)
                .linkAgreement(eservice, AgreementState.PENDING)
                .get(Agreement.class);

        return webContractValidator
                .as(User.getTenantAdmin(PRODUCER), PRODUCER)
                .on(AgreementProducerPage.class)
                .tests(contractScenario(eservice, eserviceId, descriptorId, agreement));
    }

    private Stream<WebScenario<AgreementProducerPage>> contractScenario(
            EService eservice,
            UUID eserviceId,
            UUID descriptorId,
            Agreement agreement
    ) {
        String expectedEServiceLabel = eservice.getName() + ", v. " + eservice.getActiveDescriptor().getVersion();
        return Stream.of(
                new WebScenario<>(
                        "Producer agreements list page shows the expected copies, filters, agreement row and actions",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            softly.assertThat(eserviceId)
                                    .as("Created e-service id")
                                    .isNotNull();
                            softly.assertThat(descriptorId)
                                    .as("Published descriptor id")
                                    .isNotNull();

                            softly.assertThat(page.pageDescription().read())
                                    .as("Page description")
                                    .isEqualTo("Consulta e gestisci le richieste che gli enti ti hanno inoltrato per fruire dei tuoi e-service. Le richieste che ricevi sono raccolte in questa area.");

                            softly.assertThat(page.eServiceFilterLabel().read())
                                    .as("E-service filter label")
                                    .isEqualTo("Cerca per e-service");
                            softly.assertThat(page.eServiceFilterInput().read())
                                    .as("E-service filter initial value")
                                    .isEmpty();
                            softly.assertThat(page.consumerFilterLabel().read())
                                    .as("Consumer filter label")
                                    .isEqualTo("Cerca per fruitore");
                            softly.assertThat(page.consumerFilterInput().read())
                                    .as("Consumer filter initial value")
                                    .isEmpty();
                            softly.assertThat(page.statusFilterLabel().read())
                                    .as("Status filter label")
                                    .isEqualTo("Stato della richiesta");
                            softly.assertThat(page.statusFilterInput().read())
                                    .as("Status filter initial value")
                                    .isEmpty();

                            softly.assertThat(page.agreementsTable().get().isPresent())
                                    .as("Agreements table should be present")
                                    .isTrue();
                            softly.assertThat(page.agreementsTable().getColumnHeaders())
                                    .as("Agreements table headers")
                                    .containsExactly("E-service", "Fruitore", "Stato richiesta", "");

                            List<Integer> sizeValues = page.rowsPerPageSelector().getSizeValues();
                            softly.assertThat(sizeValues.size())
                                    .as("Rows per page selector should have more than one option")
                                    .isGreaterThan(1);
                            int initialSize = page.rowsPerPageSelector().getSelectedSize();
                            softly.assertThat(initialSize)
                                    .as("Rows per page should be initialized to the lowest value")
                                    .isEqualTo(sizeValues.get(0));

                            int initialRowCount = page.agreementsTable().tBody().rows().size();
                            page.rowsPerPageSelector().setSize(sizeValues.get(1));
                            int updatedRowCount = PollingUtils.pollUntil(
                                    () -> page.agreementsTable().tBody().rows().size(),
                                    updated -> updated >= initialRowCount,
                                    Duration.ofSeconds(20),
                                    Duration.ofSeconds(1),
                                    "Records affected by new pagination size have not been rendered yet"
                            );
                            page.rowsPerPageSelector().setSize(initialSize);
                            if (updatedRowCount > initialRowCount) {
                                softly.assertThat(page.pagination().exists())
                                        .as("Pagination should be displayed when more rows than the first page are available")
                                        .isTrue();
                            } else {
                                softly.assertThat(page.pagination().exists())
                                        .as("Pagination should not be required when all %s rows fit into a single page of %s elements", updatedRowCount, initialRowCount)
                                        .isFalse();
                            }

                            AgreementProducerPage.AgreementRow agreementRow = page.findAgreementRow(
                                    eservice.getName(),
                                    eservice.getActiveDescriptor().getVersion()
                            );
                            softly.assertThat(agreementRow.cells().get(0).read().trim())
                                    .as("Agreement row e-service cell")
                                    .isEqualTo(expectedEServiceLabel);
                            softly.assertThat(agreementRow.cells().get(1).read().trim())
                                    .as("Agreement row consumer cell")
                                    .isEqualTo(Tenant.COMUNE_DI_MILANO.getName());
                            softly.assertThat(agreementRow.statusChip().text().read().trim())
                                    .as("Agreement row status chip")
                                    .isEqualTo("in attesa di approvazione");
                            softly.assertThat(agreementRow.inspectLink().read().trim())
                                    .as("Agreement row inspect link text")
                                    .isEqualTo("Visualizza");
                            softly.assertThat(agreementRow.inspectLink().getHref())
                                    .as("Agreement row inspect link href")
                                    .contains("/erogazione/richieste/" + agreement.getId());

                            softly.assertThat(agreementRow.actionMenuButton().get().isPresent())
                                    .as("Agreement row action menu button")
                                    .isTrue();
                            agreementRow.actionMenuButton().click();
                            List<String> actionMenuItems = agreementRow.actionMenu().menuListItems().stream()
                                    .map(Menu.MenuListItem::getMenuText)
                                    .map(String::trim)
                                    .toList();
                            softly.assertThat(actionMenuItems)
                                    .as("Agreement row action menu items for PENDING agreement")
                                    .containsExactlyInAnyOrder("Rifiuta", "Attiva");
                        })
                )
        );
    }
}

