package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposeFromTemplateEditPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerPurposeFromTemplateEditPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the consumer purpose from template edit page shows the expected elements
     */
    @TestFactory
    Stream<DynamicTest> consumerPurposeFromTemplateEditPageContract() {
        EService eService = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .get(EService.class);
        PurposeTemplate purposeTemplate = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createPurposeTemplate(Tenant.COMUNE_DI_MILANO, PurposeTemplateState.PUBLISHED)
                .get(PurposeTemplate.class);
        Purpose purpose = interopJourney
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createPurposeFromTemplateAndLinkToEService(purposeTemplate, eService, PurposeVersionState.DRAFT)
                .get(Purpose.class);

        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerPurposeFromTemplateEditPage.class, purpose.getId().toString(), purposeTemplate.getId().toString())
                .tests(contractScenarios(purpose, purposeTemplate));
    }

    private Stream<WebScenario<ConsumerPurposeFromTemplateEditPage>> contractScenarios(Purpose purpose, PurposeTemplate purposeTemplate) {
        return Stream.of(
                new WebScenario<>(
                        "Consumer purpose from template edit page shows the expected content",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            softly.assertThat(page.backToPurposesButton().read())
                                    .as("Back to purpose button")
                                    .isEqualTo("Torna alle finalità");
                            softly.assertThat(page.mandatoryFieldsLabel().read())
                                    .as("Mandatory fields label")
                                    .isEqualTo("*Campi obbligatori");
                            softly.assertThat(page.stepper().steps().size())
                                    .as("Stepper size")
                                    .isEqualTo(2);
                            softly.assertThat(page.stepper().steps().get(0).isActiveOnLabel())
                                    .as("Stepper 1 active")
                                    .isTrue();
                            softly.assertThat(page.stepper().steps().get(0).number().read())
                                    .as("Stepper 1 number")
                                    .isEqualTo("1");
                            softly.assertThat(page.stepper().steps().get(0).label().read())
                                    .as("Stepper 1 label")
                                    .isEqualTo("Generale");


                            softly.assertThat(page.generalStepPanel().get().isPresent())
                                    .as("General step panel must be present")
                                    .isTrue();

                            softly.assertThat(page.generalStepPanel().sectionTitle().read())
                                    .as("General step panel title")
                                    .isEqualTo("Informazioni sulla finalità");
                            softly.assertThat(page.generalStepPanel().sectionDescription().read())
                                    .as("General step panel description")
                                    .isEqualTo("Queste informazioni sono state precompilate da chi ha creato il template di finalità agevolata e non sono modificabili.");
                            softly.assertThat(page.generalStepPanel().descriptionSectionTitle().read())
                                    .as("General step panel description section title")
                                    .isEqualTo("Descrizione");
                            softly.assertThat(page.generalStepPanel().purposeNameLabel().read())
                                    .as("Purpose template name label")
                                    .isEqualTo("Nome della finalità");
                            softly.assertThat(page.generalStepPanel().purposeNameValue().read())
                                    .as("Purpose template name value")
                                    .isEqualTo(purposeTemplate.getTitle());
                            softly.assertThat(page.generalStepPanel().purposeDescriptionLabel().read())
                                    .as("Purpose template description label")
                                    .isEqualTo("Descrizione della finalità");
                            softly.assertThat(page.generalStepPanel().purposeDescriptionValue().read())
                                    .as("Purpose template description value")
                                    .isEqualTo(purposeTemplate.getDescription());
                            softly.assertThat(page.generalStepPanel().freeAccessLabel().read())
                                    .as("Purpose template free access label")
                                    .isEqualTo("Accesso a titolo gratuito");
                            softly.assertThat(page.generalStepPanel().freeAccessValue().read())
                                    .as("Purpose template free access value")
                                    .isNotBlank();
                            if (page.generalStepPanel().freeAccessValue().read().equals("Sì")) {
                                softly.assertThat(page.generalStepPanel().freeAccessReasonLabel().read())
                                        .as("Purpose template free access reason label")
                                        .isEqualTo("Motivazione del titolo gratuito");
                                softly.assertThat(page.generalStepPanel().freeAccessReasonValue().read())
                                        .as("Purpose template free access reason value")
                                        .isNotBlank();
                            }

                            softly.assertThat(page.generalStepPanel().technicalInformationSectionTitle().read())
                                    .as("Technical information section title")
                                    .isEqualTo("Informazioni tecniche");
                            softly.assertThat(page.generalStepPanel().instanceNameLabel().read())
                                    .as("Instance name label")
                                    .startsWith("Nome dell'istanza");
                            softly.assertThat(page.generalStepPanel().instanceNameInput().read())
                                    .as("Modifiable instance name input value")
                                    .isEqualTo(purpose.getTitle());

                            softly.assertThat(page.generalStepPanel().apiCallsSectionTitle().read())
                                    .as("Api calls section title")
                                    .isEqualTo("Stima chiamate API");
                            softly.assertThat(page.generalStepPanel().apiCallsSectionDescription().read())
                                    .as("Api calls section description")
                                    .startsWith("Indica il numero di chiamate previste ogni giorno per questa finalità.");
                            softly.assertThat(page.generalStepPanel().dailyCallsLabel().read())
                                    .as("Daily calls label")
                                    .startsWith("Stima numero chiamate API/giorno");
                            softly.assertThat(page.generalStepPanel().dailyCallsInput().read())
                                    .as("Daily calls input value")
                                    .isNotBlank();
                            softly.assertThat(page.generalStepPanel().dailyCallsHelpText().read())
                                    .as("Daily calls input help text")
                                    .isEqualTo("Inserisci un numero superiore o uguale a 1");

                            softly.assertThat(page.generalStepPanel().thresholdsAlertTitle().read())
                                    .as("Daily calls availability alert title")
                                    .isEqualTo("CHIAMATE API DISPONIBILI AL GIORNO");
                            softly.assertThat(page.generalStepPanel().consumerThresholdLabel().read())
                                    .as("Daily calls availability label (consumer)")
                                    .isEqualTo("Per fruitore");
                            softly.assertThat(page.generalStepPanel().consumerThresholdValue().read())
                                    .as("Daily calls availability value (consumer)")
                                    .isNotBlank();
                            softly.assertThat(page.generalStepPanel().totalThresholdLabel().read())
                                    .as("Daily calls availability label (total)")
                                    .isEqualTo("Totali");
                            softly.assertThat(page.generalStepPanel().totalThresholdValue().read())
                                    .as("Daily calls availability value (total)")
                                    .contains("chiamate disponibili su");
                            softly.assertThat(page.generalStepPanel().thresholdsAlert().read())
                                    .as("Threshold alert")
                                    .isEqualTo("Le chiamate disponibili possono variare se l'erogatore riceve altre finalità prima della tua pubblicazione.");

                            softly.assertThat(page.generalStepPanel().saveDraftAndContinueButton().read())
                                    .as("Save draft and continue button (first panel)")
                                    .isEqualTo("Salva bozza e prosegui");

                            page.generalStepPanel().saveDraftAndContinueButton().click();

                            softly.assertThat(page.riskAnalysisStepPanel().sectionTitle().read())
                                    .as("Risk analysis section title")
                                    .isEqualTo("Analisi del rischio");
                            softly.assertThat(page.riskAnalysisStepPanel().sectionDescription().read())
                                    .as("Risk analysis section description")
                                    .isEqualTo("Le domande del questionario varieranno in base alle risposte fornite man mano. Modificando la risposta a una domanda precedente, le successive domande potrebbero variare.");
                            softly.assertThat(page.riskAnalysisStepPanel().warningAlert().message().read())
                                    .as("Risk analysis section alert")
                                    .isEqualTo("Non inserire dati personali all'interno dei campi di testo libero");

                            softly.assertThat(page.riskAnalysisStepPanel().purposeQuestionLabel().read())
                                    .as("Risk analysis purpose question label")
                                    .isEqualTo("Indicare per quale finalità si intende accedere ai dati messi a disposizione con la fruizione del presente E-service*");
                            softly.assertThat(page.riskAnalysisStepPanel().purposeLockedChip().get())
                                    .as("Risk analysis purpose question locked chip")
                                    .isPresent();
                            softly.assertThat(page.riskAnalysisStepPanel().purposeQuestionHelpText().read())
                                    .as("Risk analysis purpose question help text")
                                    .isEqualTo("NB: Si ricorda ai sensi dell’art. 5, paragrafo 1, lett. b), del GDPR (principio della limitazione delle finalità), le finalità devono essere determinate, esplicite e legittime, e che i dati ottenuti possono essere successivamente trattati solo in modo compatibile con le predette finalità. Si ricorda, altresì, che qualora sussista più di una finalità, il Fruitore DEVE effettuare un’analisi del rischio per ognuna delle finalità individuate");
                            softly.assertThat(page.riskAnalysisStepPanel().institutionalPurposeRadio().label().read())
                                    .as("Institutional purpose radio label")
                                    .isEqualTo("Per fini istituzionali che non richiedano prestazioni di elaborazioni aggiuntive");
                            softly.assertThat(page.riskAnalysisStepPanel().institutionalPurposeRadio().radio().isDisabled())
                                    .as("Institutional purpose radio button should be disabled")
                                    .isTrue();
                            softly.assertThat(page.riskAnalysisStepPanel().otherPurposeRadio().label().read())
                                    .as("Other purpose radio label")
                                    .isEqualTo("Altro");
                            softly.assertThat(page.riskAnalysisStepPanel().otherPurposeRadio().radio().isDisabled())
                                    .as("Other purpose radio button should be disabled")
                                    .isTrue();

                            softly.assertThat(page.riskAnalysisStepPanel().institutionalPurposeDetailLabel().read())
                                    .as("Institutional purpose detail label")
                                    .isEqualTo("Specificare il fine perseguito per fini istituzionali che non richiedano prestazioni di elaborazioni aggiuntive*");
                            softly.assertThat(page.riskAnalysisStepPanel().suggestedAnswersSelector().read())
                                    .as("Institutional purpose detail value")
                                    .isNotBlank();

                            softly.assertThat(page.riskAnalysisStepPanel().usesPersonalDataLabel().read())
                                    .as("Uses personal data label")
                                    .isEqualTo("Indicare se si accede a dati personali*");
                            softly.assertThat(page.riskAnalysisStepPanel().usesPersonalDataLockedChip().get())
                                    .as("Uses personal data locked chip")
                                    .isPresent();
                            softly.assertThat(page.riskAnalysisStepPanel().usesPersonalDataHelpText().read())
                                    .as("Uses personal data help text")
                                    .isEqualTo("NB: si ricorda che ai sensi dell’art. 4, paragrafo 1, n. 1, del GDPR per dato personale si intende qualsiasi informazione riguardante una persona fisica identificata o che può essere identificata, direttamente o indirettamente, con particolare riferimento a un identificativo come il nome, un numero di identificazione, dati relativi all'ubicazione, un identificativo online o a uno o più elementi caratteristici della sua identità fisica, fisiologica, genetica, psichica, economica, culturale o sociale”. Pertanto devono essere considerati dati personali non solo dati che identificano direttamente un individuo (es. nome e cognome, codice fiscale, indirizzo e-mail) ma anche dati che possono essere ricondotti a un individuo solo indirettamente, tramite terzi rispetto al Fruitore (es. la targa di un veicolo, numero di matricola o altro codice alfanumerico attribuito a un individuo da un soggetto terzo).");
                            softly.assertThat(page.riskAnalysisStepPanel().usesPersonalDataYesRadio().label().read())
                                    .as("Uses personal data yes radio label")
                                    .isEqualTo("Sì");
                            softly.assertThat(page.riskAnalysisStepPanel().usesPersonalDataYesRadio().radio().isDisabled())
                                    .as("Uses personal data yes radio button should be disabled")
                                    .isTrue();
                            softly.assertThat(page.riskAnalysisStepPanel().usesPersonalDataNoRadio().label().read())
                                    .as("Uses personal data no radio label")
                                    .isEqualTo("No");
                            softly.assertThat(page.riskAnalysisStepPanel().usesPersonalDataNoRadio().radio().isDisabled())
                                    .as("Uses personal data no radio button should be disabled")
                                    .isTrue();

                            softly.assertThat(page.riskAnalysisStepPanel().usesThirdPartyDataLabel().read())
                                    .as("Uses third party data label")
                                    .isEqualTo("Indicare se si accede a dati non personali di terzi (quindi a dati non personali che non sono riferibili al Fruitore stesso)*");
                            softly.assertThat(page.riskAnalysisStepPanel().usesThirdPartyDataLockedChip().get())
                                    .as("Uses third party data locked chip")
                                    .isPresent();
                            softly.assertThat(page.riskAnalysisStepPanel().usesThirdPartyDataYesRadio().label().read())
                                    .as("Uses third party data yes radio label")
                                    .isEqualTo("Sì");
                            softly.assertThat(page.riskAnalysisStepPanel().usesThirdPartyDataYesRadio().radio().isDisabled())
                                    .as("Uses third party data yes radio button should be disabled")
                                    .isTrue();
                            softly.assertThat(page.riskAnalysisStepPanel().usesThirdPartyDataNoRadio().label().read())
                                    .as("Uses third party data no radio label")
                                    .isEqualTo("No");
                            softly.assertThat(page.riskAnalysisStepPanel().usesThirdPartyDataNoRadio().radio().isDisabled())
                                    .as("Uses third party data no radio button should be disabled")
                                    .isTrue();

                            softly.assertThat(page.riskAnalysisStepPanel().onBehalfOfThirdPartiesLabel().read())
                                    .as("On behalf of third parties label")
                                    .isEqualTo("Indicare se si intende fruire dell’E-service, anche o esclusivamente, per accedere a dati su richiesta/per conto di terzi soggetti*");
                            softly.assertThat(page.riskAnalysisStepPanel().onBehalfOfThirdPartiesLockedChip().get())
                                    .as("On behalf of third parties locked chip")
                                    .isPresent();
                            softly.assertThat(page.riskAnalysisStepPanel().onBehalfOfThirdPartiesYesRadio().label().read())
                                    .as("On behalf of third parties yes radio label")
                                    .isEqualTo("Sì");
                            softly.assertThat(page.riskAnalysisStepPanel().onBehalfOfThirdPartiesYesRadio().radio().isDisabled())
                                    .as("On behalf of third parties yes radio button should be disabled")
                                    .isTrue();
                            softly.assertThat(page.riskAnalysisStepPanel().onBehalfOfThirdPartiesNoRadio().label().read())
                                    .as("On behalf of third parties no radio label")
                                    .isEqualTo("No");
                            softly.assertThat(page.riskAnalysisStepPanel().onBehalfOfThirdPartiesNoRadio().radio().isDisabled())
                                    .as("On behalf of third parties no radio button should be disabled")
                                    .isTrue();

                            softly.assertThat(page.riskAnalysisStepPanel().backButton().read())
                                    .as("Step back button")
                                    .isEqualTo("Indietro");
                            softly.assertThat(page.riskAnalysisStepPanel().saveDraftAndContinueButton().read())
                                    .as("Save draft and continue button (second panel)")
                                    .isEqualTo("Salva bozza e prosegui");

                            softly.assertThat(page.stepper().steps().get(1).isActiveOnLabel())
                                    .as("Stepper 2 active")
                                    .isTrue();
                            softly.assertThat(page.stepper().steps().get(1).number().read())
                                    .as("Stepper 2 number")
                                    .isEqualTo("2");
                            softly.assertThat(page.stepper().steps().get(1).label().read())
                                    .as("Stepper 2 label")
                                    .isEqualTo("Analisi del rischio");
                        })
                )
        );
    }
}
