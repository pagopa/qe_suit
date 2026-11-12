package it.pagopa.interop.suite.contract;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.RiskAnalysisReviewPage;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = ALL)
@RequiredArgsConstructor
@DisplayName("Contract Test per pagina Informazioni finalità in Analisi del Rischio")
class WebRiskAnalysisReviewContractTest {

    private static final Iterable<String> EXPECTED_BREADCRUMB_LABELS = List.of("Analisi del rischio", "Informazioni finalità");
    private static final String EXPECTED_RA_BREADCRUMB_HREF = "/ui/it/analisi-del-rischio";
    private static final String PAGE_TITLE = "Informazioni finalità";

    private static final String LABEL_ESERVICE = "E-service da associare";
    private static final String LABEL_PROVIDER = "Erogatore";
    private static final String LABEL_PURPOSE = "Nome della finalità";
    private static final String LABEL_DESC = "Descrizione finalità";
    private static final String LABEL_ACCESS = "Come metterai a disposizione l'accesso ai dati di questo e-service?";
    private static final String LABEL_FREE_REASON = "Motivazione titolo gratuito";

    private static final String LABEL_API_ESTIMATE = "Stima numero chiamate API/giorno";

    private static final String LABEL_ASSIGNMENT_DATE = "Data assegnazione";
    private static final String LABEL_ASSIGNED_TO = "Assegnata a";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> testPurposeInfoPage() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_to_compile");
        User reviewer = User.getTenantUser(Tenant.SOGECAP, UserRole.REVIEWER);
        String riskAnalysisId = interopJourney
                .withConsumer(Tenant.SOGECAP, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(eServiceName, Tenant.COMUNE_DI_MILANO, Tenant.SOGECAP, reviewer)
                .get(Purpose.class).getId().toString();

        return webContractValidator
                .as(reviewer, Tenant.SOGECAP)
                .on(RiskAnalysisReviewPage.class, riskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Verify the correct rendering of the Purpose Information UI by a REVIEWER",
                                page -> {
                                },
                                page -> {
                                    verifyHeaderAndBreadcrumb(page);
                                    page.pageTitle().readAndAssert(PAGE_TITLE);

                                    verifyGeneralInfoCard(page.generalInfoCard());
                                    verifyApiEstimateCard(page.apiEstimateCard());
                                    verifyAssignmentCard(page.assignmentCard());

                                    assertThat(page.startCompilationBtn().isDisabled()).as("Pulsante Inizia compilazione abilitato").isFalse();
                                    page.startCompilationBtn().readAndAssert("Inizia la compilazione");
                                }
                        )
                ));
    }

    private void verifyHeaderAndBreadcrumb(RiskAnalysisReviewPage page) {
        assertThat(page.backToEServicesButton().isDisabled())
                .as("Button enabled").isFalse();
        page.backToEServicesButton().readAndAssert("Torna alle analisi del rischio");

        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);

        assertThat(page.breadcrumbs().links()).hasSize(1);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Breadcrumb href")
                .isEqualTo(EXPECTED_RA_BREADCRUMB_HREF);
    }

    private void verifyGeneralInfoCard(RiskAnalysisReviewPage.GeneralInfoCard card) {
        card.title().readAndAssert("Informazioni generali");

        card.eserviceLabel().readAndAssert(LABEL_ESERVICE);
        assertNotBlank(card.eserviceValue().read(), LABEL_ESERVICE);
        assertNotBlank(card.eserviceValue().getHref(), "Href e-service");
        assertThat(card.eserviceValue().get(Gettable.FindPolicy.CLICKABLE))
                .as("E-service link must be visible and clickable")
                .isPresent();


        card.providerLabel().readAndAssert(LABEL_PROVIDER);
        assertNotBlank(card.providerValue().read(), LABEL_PROVIDER);

        card.purposeNameLabel().readAndAssert(LABEL_PURPOSE);
        assertNotBlank(card.purposeNameValue().read(), LABEL_PURPOSE);

        card.purposeDescLabel().readAndAssert(LABEL_DESC);
        assertNotBlank(card.purposeDescValue().read(), LABEL_DESC);

        card.accessLabel().readAndAssert(LABEL_ACCESS);
        assertNotBlank(card.accessValue().read(), LABEL_ACCESS);

        card.freeReasonLabel().readAndAssert(LABEL_FREE_REASON);
        assertNotBlank(card.freeReasonValue().read(), LABEL_FREE_REASON);
    }

    private void verifyApiEstimateCard(RiskAnalysisReviewPage.APIEstimateCard card) {
        card.title().readAndAssert("Stima chiamate API");

        card.apiEstimateLabel().readAndAssert(LABEL_API_ESTIMATE);
        assertNotBlank(card.apiEstimateValue().read(), LABEL_API_ESTIMATE);
    }

    private void verifyAssignmentCard(RiskAnalysisReviewPage.AssignmentCard card) {
        card.title().readAndAssert("Assegnazione");

        card.assignmentDateLabel().readAndAssert(LABEL_ASSIGNMENT_DATE);
        assertNotBlank(card.assignmentDateValue().read(), LABEL_ASSIGNMENT_DATE);

        card.assignedToLabel().readAndAssert(LABEL_ASSIGNED_TO);
        assertNotBlank(card.assignedToValue().read(), LABEL_ASSIGNED_TO);
    }


    private static void assertNotBlank(String value, String description) {
        assertThat(value).as("%s deve essere valorizzato", description).isNotBlank();
    }

}
