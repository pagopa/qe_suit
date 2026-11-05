package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
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
public class WebEServiceAPISpecsDownloadContractTest {
    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

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

}
