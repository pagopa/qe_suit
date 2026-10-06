package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.refactor.EServiceCreatePage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.RandomUtils;
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
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceCreatePageContractTest {

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Verifies the links and copy shown in the "required attributes" section of the
     * e-service creation wizard (step "Soglie e attributi").
     * <p>
     * Each scenario logs in as a tenant admin of Comune di Milano, fills the general data step
     * with a random name and description (no personal data), saves the draft and then
     * asserts on the attributes step:
     * <ul>
     *   <li>the "Scopri di più sugli attributi" link points to the attributes technical reference;</li>
     *   <li>the certified attribute tab shows the expected description and its
     *       "Scopri di più su quali attributi usare" link points to the most used attributes guide.</li>
     * </ul>
     *
     * @return a stream of dynamic tests, one for each checked link or description
     * @implNote Covers [PIN-10560].
     */
    @TestFactory
    Stream<DynamicTest> attributesSectionMustShowCorrectLinksAndCopy() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(EServiceCreatePage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "The attribute link 'Scopri di piu sugli attributi' must be correct",
                                creationPage -> creationPage.generalDataForm()
                                        .setEServiceName(RandomUtils.randomAlphanumericName("name"))
                                        .setEServiceDescription(RandomUtils.randomAlphanumericName("description"))
                                        .setUsePersonalData(false)
                                        .saveDraft(),
                                creationPage -> {
                                    var thresholdAndAttributeForm = creationPage.thresholdAndAttributeForm();
                                    thresholdAndAttributeForm.assertLoaded();

                                    String attributeHref = creationPage.thresholdAndAttributeForm()
                                            .requiredAttributeSection()
                                            .attributeLink()
                                            .getHref();

                                    Assertions.assertThat(attributeHref).as("The attribute link must be correct")
                                            .isEqualTo("https://developer.pagopa.it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi");
                                }
                        ),
                        new WebScenario<>(
                                "The description of the certified attribute and the link 'Scopri di più su quali attributi usare.' must be corrected.",
                                creationPage -> creationPage.generalDataForm()
                                        .setEServiceName(RandomUtils.randomAlphanumericName("name"))
                                        .setEServiceDescription(RandomUtils.randomAlphanumericName("description"))
                                        .setUsePersonalData(false)
                                        .saveDraft(),
                                creationPage -> {
                                    var thresholdAndAttributeForm = creationPage.thresholdAndAttributeForm();
                                    thresholdAndAttributeForm.assertLoaded();

                                    String certifiedAttributeHref = creationPage.thresholdAndAttributeForm()
                                            .requiredAttributeSection()
                                            .certifiedAttributeTabPanel()
                                            .findOutMoreAboutAttributesToUseLink()
                                            .getHref();

                                    String certifiedAttributeDescription = creationPage.thresholdAndAttributeForm()
                                            .requiredAttributeSection()
                                            .certifiedAttributeTabPanel()
                                            .description()
                                            .read();

                                    Assertions.assertThat(certifiedAttributeHref).as("The attribute link must be correct")
                                            .isEqualTo("https://developer.pagopa.it/it/pdnd-interoperabilita/guides/manuale-operativo-pdnd-interoperabilita/v1.0/riferimenti-tecnici/attributi/gli-attributi-piu-utilizzati");

                                    Assertions.assertThat(certifiedAttributeDescription).as("The certified attribute description must be correct")
                                            .isEqualTo("Sono attributi riconosciuti da fonti certificate come IPA. Tra i più usati, per esempio, ci sono Comuni e loro Consorzi e Associazioni, Province e loro Consorzi e Associazioni, Gestori di Pubblici Servizi. Scopri di più su quali attributi usare.");
                                }
                        )
                ));
    }


}
