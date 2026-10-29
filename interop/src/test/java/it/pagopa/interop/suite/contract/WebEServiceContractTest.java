package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDraftEditPage;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceDetailPage;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceListPage;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCatalogPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.async.DelayUtils;
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
public class WebEServiceContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> shouldLoadProducerEServiceList() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(ProducerEServiceListPage.class)
                        .tests(Stream.of(new WebScenario<>(
                                "e-service erogati accessibili all'utente " + role.name() + " del Comune di Milano",
                                page -> DelayUtils.waitForSeconds(10),
                                ProducerEServiceListPage::assertLoaded
                        ))));
    }

    @TestFactory
    Stream<DynamicTest> shouldLoadEServiceCatalog() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(EServiceCatalogPage.class)
                        .tests(Stream.of(new WebScenario<>(
                                "catalogo e-service accessibile all'utente " + role.name() + " del Comune di Milano",
                                page -> DelayUtils.waitForSeconds(10),
                                EServiceCatalogPage::assertLoaded
                        ))));
    }

    @TestFactory
    Stream<DynamicTest> shouldShowEServiceDetailToProducer() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        EService eService = interopJourney
                .withProducer(tenant, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);

        return Stream.of(UserRole.ADMIN, UserRole.API)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(
                                ProducerEServiceDetailPage.class,
                                eService.getId().toString(),
                                eService.getActiveDescriptor().getId().toString()
                        )
                        .tests(Stream.of(new WebScenario<>(
                                "nome dell'e-service creato visibile all'utente " + role.name() + " del Comune di Milano",
                                page -> DelayUtils.waitForSeconds(10),
                                page -> {
                                    page.assertLoaded();
                                    page.pageTitle().readAndAssert(eService.getName());
                                }
                        ))));
    }

    @TestFactory
    Stream<DynamicTest> shouldDisplayRestInterfaceAllowedFormatsHint() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        EService eService = interopJourney
                .withProducer(tenant, UserRole.ADMIN)
                .createEService(cmd -> cmd.technology(EServiceTechnology.REST))
                .get(EService.class);

        return webContractValidator
                .as(User.getTenantAdmin(tenant), tenant)
                .on(
                        EServiceDraftEditPage.class,
                        eService.getId().toString(),
                        eService.getLastDraftDescriptor().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "formati consentiti per l'interfaccia di un e-service REST mostrati nello step specifiche tecniche",
                        page -> {
                            page.generalDataForm().saveDraft();
                            page.thresholdAndAttributeForm().assertLoaded();
                            page.thresholdAndAttributeForm().saveDraftButton().click();
                            page.technicalSpecsForm().assertLoaded();
                        },
                        page -> page.technicalSpecsForm()
                                .formatsHint()
                                .readAndAssert("Puoi caricare solo file con estensione .yaml, .json")
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldDisplaySoapInterfaceAllowedFormatsHint() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        EService eService = interopJourney
                .withProducer(tenant, UserRole.ADMIN)
                .createEService(cmd -> cmd.technology(EServiceTechnology.SOAP))
                .get(EService.class);

        return webContractValidator
                .as(User.getTenantAdmin(tenant), tenant)
                .on(
                        EServiceDraftEditPage.class,
                        eService.getId().toString(),
                        eService.getLastDraftDescriptor().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "formati consentiti per l'interfaccia di un e-service SOAP mostrati nello step specifiche tecniche",
                        page -> {
                            page.generalDataForm().saveDraft();
                            page.thresholdAndAttributeForm().assertLoaded();
                            page.thresholdAndAttributeForm().saveDraftButton().click();
                            page.technicalSpecsForm().assertLoaded();
                        },
                        page -> page.technicalSpecsForm().formatsHint().readAndAssert("Puoi caricare solo file con estensione .wsdl, .xml")
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldDisplayDocumentAllowedFormatsHint() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;
        EService eService = interopJourney
                .withProducer(tenant, UserRole.ADMIN)
                .createEService(cmd -> cmd.technology(EServiceTechnology.REST))
                .updateDescriptor(cmd -> {}, EServiceDescriptorState.DRAFT)
                .linkOpenApiInterface()
                .get(EService.class);

        return webContractValidator
                .as(User.getTenantAdmin(tenant), tenant)
                .on(
                        EServiceDraftEditPage.class,
                        eService.getId().toString(),
                        eService.getLastDraftDescriptor().getId().toString()
                )
                .tests(Stream.of(new WebScenario<>(
                        "formati consentiti per i documenti allegati di un e-service mostrati nello step informazioni aggiuntive",
                        page -> {
                            page.generalDataForm().saveDraft();
                            page.thresholdAndAttributeForm().assertLoaded();
                            page.thresholdAndAttributeForm().saveDraftButton().click();
                            page.technicalSpecsForm().assertLoaded();
                            page.technicalSpecsForm().saveDraftButton().click();
                            page.additionalInfoForm().assertLoaded();
                        },
                        page -> page.additionalInfoForm().documentFormatsHint().readAndAssert("Puoi caricare solo file con estensione .pdf, .json, .md, .yaml, .yml, .txt, .xsd, .wsdl, .xml")
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldDisplayImportAllowedFormatsHint() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;

        return webContractValidator
                .as(User.getTenantAdmin(tenant), tenant)
                .on(ProducerEServiceListPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "formati consentiti per l'importazione di un e-service mostrati nel drawer importa",
                        page -> page.importButton().click(),
                        page -> page.importFormatsHint().readAndAssert("Puoi caricare solo file con estensione .zip")
                )));
    }
}