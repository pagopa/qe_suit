package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceTemplateAPISpecsDownloadContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

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

}
