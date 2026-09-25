package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.web.eservice.application.WebEServiceTechnicalData;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCreationPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * WEB contract test for the "microcopy area di upload" (helper-text sotto il controllo
 * di upload documento generico) esposto dall'ultima pagina del wizard di creazione
 * e-service ({@link it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard.AdditionalDataWizard}).
 * <p>
 * Contratto verificato (tecnico-UI, indipendente dal flusso di business):
 * <pre>
 *     "Puoi caricare solo file con estensione .pdf .json .md .yaml .yml .txt .xsd .wsdl .xml"
 * </pre>
 */
@Execution(ExecutionMode.CONCURRENT)
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
public class WebEServiceDocumentUploadContractTest {

    private static final String DOCUMENT_FORMATS_HINT =
            "Puoi caricare solo file con estensione .pdf .json .md .yaml .yml .txt .xsd .wsdl .xml";

    private static final String NAME = "eservice-" + UUID.randomUUID();
    private static final String DESCRIPTION = "E-service creato per verificare il contratto UI di upload documenti generici";

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateDocumentUploadFormatsHint() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(EServiceCreationPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<EServiceCreationPage>> scenarios() {
        String interfaceAttachmentPath = WebEServiceTechnicalData.buildDefault().interfaceAttachmentPath();

        return Stream.of(
                new WebScenario<>(
                        "upload documenti generici (ultima pagina del wizard)",
                        page -> {
                            page.generalDataStep()
                                    .setName(NAME)
                                    .setDescription(DESCRIPTION)
                                    .setTechnology(EServiceTechnology.REST)
                                    .setAsyncExchange(false)
                                    .setMode(EServiceMode.DELIVER)
                                    .setPersonalData(false);
                            page.saveDraftButton().click(); // step 1 -> step ? (probe)

                            probeCurrentStep(page, "after 1 click");

                            page.technicalSpecificationStep()
                                    .interfaceComponent()
                                    .uploadApiInterface(interfaceAttachmentPath);
                            page.saveDraftButton().click(); // step 3 -> step 4 (documenti generici)
                        },
                        page -> Assertions.assertThat(
                                page.additionalInformationStep()
                                        .documentFormatsHint()
                                        .read()
                        ).isEqualTo(DOCUMENT_FORMATS_HINT)
                )
        );
    }

    private void probeCurrentStep(EServiceCreationPage page, String label) {
        try {
            String t = page.thresholdAndAttributeStep().title().read();
            System.out.println("PROBE[" + label + "] threshold title = " + t);
        } catch (Exception e) {
            System.out.println("PROBE[" + label + "] threshold NOT present: " + e.getClass().getSimpleName());
        }
        try {
            String h = page.technicalSpecificationStep().voucherComponent().getAudienceHelperText();
            System.out.println("PROBE[" + label + "] technicalSpec/voucher audience helper = " + h);
        } catch (Exception e) {
            System.out.println("PROBE[" + label + "] technicalSpec/voucher NOT present: " + e.getClass().getSimpleName());
        }
        try {
            String hint = page.additionalInformationStep().documentFormatsHint().read();
            System.out.println("PROBE[" + label + "] additionalInformation hint = " + hint);
        } catch (Exception e) {
            System.out.println("PROBE[" + label + "] additionalInformation NOT present: " + e.getClass().getSimpleName());
        }
    }
}

