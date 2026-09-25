package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.MyEServiceCatalogPage;
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
 * di upload del file .zip) esposto dal drawer di importazione e-service
 * ({@link it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceImportDrawer}),
 * raggiungibile dal button "Importa" nella pagina "I tuoi e-service"
 * ({@link MyEServiceCatalogPage}, {@code /erogazione/e-service}).
 * <p>
 * Contratto verificato (tecnico-UI, indipendente dal flusso di business):
 * <pre>
 *     microcopy sotto la zona di upload contenente ".zip"
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
public class WebEServiceImportContractTest {

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateImportUploadFormatsHint() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(MyEServiceCatalogPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<MyEServiceCatalogPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "upload file .zip nel drawer di importazione e-service",
                        MyEServiceCatalogPage::openImportDrawer,
                        page -> Assertions.assertThat(
                                page.importDrawer()
                                        .formatsHint()
                                        .read()
                        ).contains(".zip")
                )
        );
    }
}



