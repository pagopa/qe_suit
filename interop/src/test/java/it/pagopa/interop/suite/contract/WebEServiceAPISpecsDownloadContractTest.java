package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDownloadFixtureFactory;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
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
public class WebEServiceAPISpecsDownloadContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final BffEServiceDownloadFixtureFactory downloadFixtureFactory;

    @TestFactory
    Stream<DynamicTest> shouldDisplayCorrectNameForPublishedEService() {
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        EService eService = interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);

        String expectedApiSpecName = "Specifica API_"
                + eService.getName() + "_"
                + producer.getName() + "_v"
                + eService.getActiveDescriptor().getVersion();

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(producer, role), producer)
                        .on(
                                ProducerEServiceDetailPage.class,
                                eService.getId().toString(),
                                eService.getActiveDescriptor().getId().toString()
                        )
                        .tests(Stream.of(new WebScenario<>(
                                "nome specifica API dell'e-service pubblicato visibile all'utente " + role.name() + " del Comune di Milano",
                                ProducerEServiceDetailPage::assertLoaded,
                                page -> page.apiSpecsDownloadButton().readAndAssert(expectedApiSpecName)
                        ))));
    }


    @TestFactory
    Stream<DynamicTest> shouldAnnounceDocumentNameBeforeDownloadAction() {
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        return Stream.of(false, true).flatMap(async -> {
            var fixture = downloadFixtureFactory.create(producer, async, true);
            return webContractValidator
                    .as(User.getTenantAdmin(producer), producer)
                    .on(ProducerEServiceDetailPage.class,
                            fixture.resourceId().toString(), fixture.versionId().toString())
                    .tests(Stream.of(new WebScenario<>(
                            "Accessibilità - documentazione e-service " + (async ? "asincrono" : "sincrono"),
                            page -> {},
                            page -> {
                                assertDownloadNames(page.documentationDownloadButtons(),
                                        fixture.documentationNames(), "Documentazione e-service");
                                if (async) {
                                    assertDownloadNames(List.of(page.callbackDownloadButton()),
                                            List.of(fixture.callbackName()), "Interfaccia di callback");
                                }
                            }
                    )));
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
