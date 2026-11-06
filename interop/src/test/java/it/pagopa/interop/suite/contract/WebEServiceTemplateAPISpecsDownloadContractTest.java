package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateDownloadFixtureFactory;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice_template.infrastructure.page.ProducerEServiceTemplateDetailPage;
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
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceTemplateAPISpecsDownloadContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceTemplateDownloadFixtureFactory downloadFixtureFactory;

    @TestFactory
    Stream<DynamicTest> shouldDisplayCorrectNameForPublishedEServiceTemplate() {
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        EServiceTemplate template = interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .get(EServiceTemplate.class);

        String expectedApiSpecName = "Specifica API_"
                + template.getName() + "_"
                + producer.getName() + "_v"
                + template.getLastPublishedVersion().getVersion();

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(producer, role), producer)
                        .on(
                                ProducerEServiceTemplateDetailPage.class,
                                template.getId().toString(),
                                template.getLastPublishedVersion().getId().toString()
                        )
                        .tests(Stream.of(new WebScenario<>(
                                "nome specifica API dell'e-service template pubblicato visibile all'utente " + role.name() + " del Comune di Milano",
                                ProducerEServiceTemplateDetailPage::assertLoaded,
                                page -> page.apiSpecsDownloadButton().readAndAssert(expectedApiSpecName)
                        ))));
    }


    @TestFactory
    Stream<DynamicTest> shouldAnnounceDocumentNameBeforeDownloadAction() {
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        var fixture = downloadFixtureFactory.create(producer, false, true);
        return webContractValidator
                .as(User.getTenantAdmin(producer), producer)
                .on(ProducerEServiceTemplateDetailPage.class,
                        fixture.resourceId().toString(), fixture.versionId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Accessibilità - documenti del template: nome prima dell'azione",
                        page -> {},
                        page -> assertDownloadNames(page.documentationDownloadButtons(),
                                fixture.documentationNames(), "Documentazione template")
                )));
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
