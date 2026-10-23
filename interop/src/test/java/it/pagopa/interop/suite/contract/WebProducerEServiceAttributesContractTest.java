package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.eservice_template.infrastructure.page.ProducerEServiceTemplateDetailPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
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
        properties = {"spring.profiles.include=junit","channel.web.browser=chrome","channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerEServiceAttributesContractTest {

    private static final Tenant TENANT = Tenant.COMUNE_DI_MILANO;

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> shouldSaveEServiceAttributesWithoutChanges() {
        EService eService = interopJourney
                .withProducer(TENANT, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);

        return webContractValidator
                .as(User.getTenantAdmin(TENANT), TENANT)
                .on(
                        ProducerEServiceDetailPage.class,
                        eService.getId().toString(),
                        eService.getActiveDescriptor().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "salvataggio attributi e-service senza modifiche non restituisce errore",
                        page -> {
                            page.addAttributesButton().click();
                            page.attributesDrawerSaveButton().click();
                        },
                        page -> Assertions.assertThat(page.snackbar().alert().isError())
                                .as("Snackbar di errore dopo il salvataggio senza modifiche")
                                .isFalse()
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldSaveEServiceTemplateAttributesWithoutChanges() {
        EServiceTemplate eServiceTemplate = interopJourney
                .withProducer(TENANT, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .get(EServiceTemplate.class);

        return webContractValidator
                .as(User.getTenantAdmin(TENANT), TENANT)
                .on(
                        ProducerEServiceTemplateDetailPage.class,
                        eServiceTemplate.getId().toString(),
                        eServiceTemplate.getLatestVersion().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "salvataggio attributi template e-service senza modifiche non restituisce errore",
                        page -> {
                            page.addAttributesButton().click();
                            page.attributesDrawerSaveButton().click();
                        },
                        page -> Assertions.assertThat(page.snackbar().alert().isError())
                                .as("Snackbar di errore dopo il salvataggio senza modifiche")
                                .isFalse()
                )));
    }
}

