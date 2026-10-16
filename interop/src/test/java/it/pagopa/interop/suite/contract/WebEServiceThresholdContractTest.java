package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.application.WebEServiceGeneralData;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage;

import java.util.function.Consumer;
import java.util.stream.Stream;

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
public class WebEServiceThresholdContractTest {

    private static final String INTEGER_THRESHOLD_ERROR_MESSAGE = "Il valore di soglia deve essere intero";
    private static final String DAILY_CALLS_PER_CONSUMER_ERROR_ID = "dailyCallsPerConsumer-error";
    private static final String DAILY_CALLS_TOTAL_ERROR_ID = "dailyCallsTotal-error";

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateThresholdFields() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(EServiceCreatePage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<EServiceCreatePage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "soglia per fruitore con valore decimale (virgola)",
                        fillGeneralDataThen(page -> fillThresholdsAndSave(page, "1,5", "100")), // "," è omesso, ma non il "."
                        page -> Assertions.assertThat(
                                getDailyCallsPerConsumerErrorText(page)
                        ).isNullOrEmpty()
                ),

                new WebScenario<>(
                        "soglia per fruitore con valore decimale (punto)",
                        fillGeneralDataThen(page -> fillThresholdsAndSave(page, "1.5", "100")),
                        page -> Assertions.assertThat(
                                getDailyCallsPerConsumerErrorText(page)
                        ).isEqualTo(INTEGER_THRESHOLD_ERROR_MESSAGE)
                ),

                new WebScenario<>(
                        "soglia totale con valore decimale (virgola)",
                        fillGeneralDataThen(page -> fillThresholdsAndSave(page, "1", "1,5")), // "," è omesso, ma non il "."
                        page -> Assertions.assertThat(
                                getDailyCallsTotalErrorText(page)
                        ).isNullOrEmpty()
                ),

                new WebScenario<>(
                        "soglia totale con valore decimale (punto)",
                        fillGeneralDataThen(page -> fillThresholdsAndSave(page, "1", "1.5")),
                        page -> Assertions.assertThat(
                                getDailyCallsTotalErrorText(page)
                        ).isEqualTo(INTEGER_THRESHOLD_ERROR_MESSAGE)
                )
        );
    }

    private Consumer<EServiceCreatePage> fillGeneralDataThen(Consumer<EServiceCreatePage> thresholdAction) {
        return page -> {
            fillValidGeneralData(page);
            page.generalDataForm().saveDraft();
            thresholdAction.accept(page);
        };
    }

    private void fillValidGeneralData(EServiceCreatePage page) {
        WebEServiceGeneralData model = WebEServiceGeneralData.buildDefault();

        // La modalità va impostata prima dei dati personali, che ne dipendono
        page.generalDataForm()
                .setEServiceName(model.eservice().getName())
                .setEServiceDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setMode(model.eservice().getMode())
                .setUsePersonalData(Boolean.TRUE.equals(model.eservice().getPersonalData()));
    }

    private void fillThresholdsAndSave(EServiceCreatePage page, String dailyCallsPerConsumer, String dailyCallsTotal) {
        var thresholdSection = page.thresholdAndAttributeForm().apiCallThresholdSection();
        thresholdSection.dailyCallsPerConsumerInput().cleanAndWrite(dailyCallsPerConsumer);
        thresholdSection.dailyCallsTotalInput().cleanAndWrite(dailyCallsTotal);
        page.thresholdAndAttributeForm().saveDraftButton().click();
    }

    private String getDailyCallsPerConsumerErrorText(EServiceCreatePage page) {
        return page.thresholdAndAttributeForm().apiCallThresholdSection()
                .dailyCallsPerConsumerInput().getErrorMessage(DAILY_CALLS_PER_CONSUMER_ERROR_ID);
    }

    private String getDailyCallsTotalErrorText(EServiceCreatePage page) {
        return page.thresholdAndAttributeForm().apiCallThresholdSection()
                .dailyCallsTotalInput().getErrorMessage(DAILY_CALLS_TOTAL_ERROR_ID);
    }
}
