package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplatesCatalogPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceTemplateCatalogCard;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerEServiceTemplatesCatalogPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the producer e-service templates catalog page shows the expected
     * breadcrumbs, title, description, filters, and published template card.
     */
    @TestFactory
    Stream<DynamicTest> producerEServiceTemplatesCatalogPageContract() {
        EServiceTemplate template = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .waitUntilEServiceTemplate(t ->
                        t.getLatestVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                .get(EServiceTemplate.class);
        String eserviceTemplateId = template.getId().toString();
        String eserviceTemplateVersionId = template.getLastPublishedVersion().getId().toString();

        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ProducerEServiceTemplatesCatalogPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Producer e-service templates catalog page shows expected copy, filters and published template card",
                                page -> {
                                    page.nameFilterInput().fill(template.getName());
                                    page.searchButton().click();
                                    PollingUtils.pollUntil(
                                            () -> page.findCardByTitle(template.getName()),
                                            Optional::isPresent,
                                            "Published e-service template card not found after filtering by name: " + template.getName()
                                    );
                                },
                                page -> assertSoftly(softly -> {
                                    softly.assertThat(page.breadcrumbs().getPath())
                                            .as("Breadcrumbs path")
                                            .isEqualTo("Erogazione/Template e-service");

                                    softly.assertThat(page.pageTitle().read())
                                            .as("Page title")
                                            .isEqualTo("Catalogo dei template di e-service");
                                    softly.assertThat(page.pageDescription().read())
                                            .as("Page description")
                                            .isEqualTo("I template ti permettono di creare istanze di e-service partendo da un modello preesistente.");

                                    softly.assertThat(page.nameFilterLabel().read())
                                            .as("Name filter label")
                                            .isEqualTo("Cerca per nome");
                                    softly.assertThat(page.nameFilterInput().get(Component.FindPolicy.PRESENT).isPresent())
                                            .as("Name filter input should be present")
                                            .isTrue();
                                    softly.assertThat(page.creatorFilterLabel().read())
                                            .as("Creator filter label")
                                            .isEqualTo("Cerca per creatore template");
                                    softly.assertThat(page.creatorFilterInput().get(Component.FindPolicy.PRESENT).isPresent())
                                            .as("Creator filter input should be present")
                                            .isTrue();
                                    softly.assertThat(page.searchButton().isDisabled())
                                            .as("Search button should be disabled")
                                            .isTrue();

                                    Optional<EServiceTemplateCatalogCard> cardOptional = page.findCardByTitle(template.getName());
                                    softly.assertThat(cardOptional)
                                            .as("Published template card should be present")
                                            .isPresent();

                                    cardOptional.ifPresent(card -> {
                                        softly.assertThat(card.title().read())
                                                .as("Template card title")
                                                .isEqualTo(template.getName());
                                        softly.assertThat(card.description().read())
                                                .as("Template card description")
                                                .isEqualTo(template.getDescription());
                                        softly.assertThat(card.producerName().read())
                                                .as("Template card producer name")
                                                .isNotBlank();
                                        softly.assertThat(card.inspectLink().read())
                                                .as("Template card inspect link text")
                                                .isEqualTo("Visualizza");
                                        softly.assertThat(card.isInspectLinkDisabled())
                                                .as("Template card inspect link should be enabled")
                                                .isFalse();
                                        softly.assertThat(card.inspectLink().getHref())
                                                .as("Template card inspect link href")
                                                .contains("/erogazione/catalogo-template/" + eserviceTemplateId + "/" + eserviceTemplateVersionId);
                                    });
                                })
                        )
                ));
    }
}

