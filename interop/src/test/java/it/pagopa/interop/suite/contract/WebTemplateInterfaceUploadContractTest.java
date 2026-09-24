package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.web.eservice_template.page.TemplateCreationPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * WEB contract test for the "microcopy area di upload" (helper-text sotto il controllo
 * di upload interfaccia) esposto da {@link it.pagopa.interop.web.infrastructure.config.suit.component.InterfaceComponent}
 * nel flusso di creazione Template E-service, tramite {@link TemplateCreationPage}.
 * <p>
 * Il wizard di creazione prevede 3 step: dati generali (su questo URL), conferma
 * (solo click), upload interfaccia. Step 2 e 3 avvengono dopo redirect applicativo
 * verso una pagina strutturalmente identica a
 * {@link it.pagopa.interop.web.eservice_template.page.TemplateEServiceDetailErogatorePage},
 * risolta via XPath sul DOM live (nessun id necessario).
 * <p>
 * Contratto verificato (tecnico-UI, indipendente dal flusso di business):
 * <pre>
 *     "Puoi caricare solo file con estensione &lt;estensioni&gt;"
 * </pre>
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebTemplateInterfaceUploadContractTest {

    private static final String REST_FORMATS_HINT = "Puoi caricare solo file con estensione .yaml .yml .json";
    private static final String SOAP_FORMATS_HINT = "Puoi caricare solo file con estensione .wsdl .xml";

    private static final String NAME = "Contract test upload interfaccia template";
    private static final String INTENDED_TARGET = "Contract test - destinatari";
    private static final String DESCRIPTION = "Template creato per verificare il contratto UI di upload interfaccia";

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateInterfaceUploadFormatsHint() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(TemplateCreationPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<TemplateCreationPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "upload interfaccia REST/OpenAPI",
                        page -> {
                            page.generalDataStep()
                                    .setName(NAME)
                                    .setIntendedTarget(INTENDED_TARGET)
                                    .setDescription(DESCRIPTION)
                                    .setTechnology(EServiceTechnology.REST)
                                    .setPersonalData(false);
                            page.saveDraftButton().click(); // step 1 -> step 2 (redirect applicativo)
                            page.saveDraftButton().click(); // step 2 -> step 3 (solo click)
                        },
                        page -> Assertions.assertThat(
                                page.interfaceComponent()
                                        .formatsHint()
                                        .read()
                        ).isEqualTo(REST_FORMATS_HINT)
                ),

                new WebScenario<>(
                        "upload interfaccia SOAP/WSDL",
                        page -> {
                            page.generalDataStep()
                                    .setName(NAME)
                                    .setIntendedTarget(INTENDED_TARGET)
                                    .setDescription(DESCRIPTION)
                                    .setTechnology(EServiceTechnology.SOAP)
                                    .setPersonalData(false);
                            page.saveDraftButton().click();
                            page.saveDraftButton().click();
                        },
                        page -> Assertions.assertThat(
                                page.interfaceComponent()
                                        .formatsHint()
                                        .read()
                        ).isEqualTo(SOAP_FORMATS_HINT)
                )
        );
    }
}



