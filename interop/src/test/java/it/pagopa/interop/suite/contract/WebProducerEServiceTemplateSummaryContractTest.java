package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateDownloadFixtureFactory;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice_template.infrastructure.page.ProducerEServiceTemplateSummaryPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerEServiceTemplateSummaryContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final BffEServiceTemplateDownloadFixtureFactory downloadFixtureFactory;

    @TestFactory
    Stream<DynamicTest> shouldAnnounceDraftDocumentNameBeforeDownloadAction() {
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        return Stream.of(false, true).flatMap(async -> {
            var fixture = downloadFixtureFactory.create(producer, async, false);
            return webContractValidator
                    .as(User.getTenantAdmin(producer), producer)
                    .on(ProducerEServiceTemplateSummaryPage.class,
                            fixture.resourceId().toString(), fixture.versionId().toString())
                    .tests(Stream.of(
                            new WebScenario<>("Accessibilità - specifiche tecniche template, async=" + async,
                                    page -> page.technicalSpecsCard().expand(),
                                    page -> assertDownloadNames(
                                            page.technicalSpecsCard().content().documentDownloadButtons(),
                                            fixture.technicalSpecificationNames(), "Specifiche tecniche template")),
                            new WebScenario<>("Accessibilità - allegati aggiuntivi template, async=" + async,
                                    page -> page.additionalInfoCard().expand(),
                                    page -> assertDownloadNames(
                                            page.additionalInfoCard().content().documentDownloadButtons(),
                                            fixture.additionalDocumentNames(), "Informazioni aggiuntive template"))
                    ));
        });
    }

    private void assertDownloadNames(List<Button> buttons, List<String> expected, String section) {
        Assertions.assertThat(buttons).as(section + " - numero documenti").hasSize(expected.size());
        Assertions.assertThat(buttons.stream().map(Button::read).toList())
                .as(section + " - nomi visibili")
                .containsExactlyInAnyOrderElementsOf(expected);
        for (String name : expected) {
            Assertions.assertThat(buttons).filteredOn(button -> name.equals(button.read()))
                    .as(section + " - controllo del documento " + name)
                    .singleElement()
                    .satisfies(button -> Assertions.assertThat(button.readAccessibleName())
                            .as(section + " - nome accessibile di " + name)
                            .isEqualTo(name + ", scarica l'allegato"));
        }
    }
}
