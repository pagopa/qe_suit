package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.application.WebEServiceGeneralData;
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

import java.util.function.Consumer;
import java.util.stream.Stream;

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
public class WebEServiceThresholdContractTest {

    private static final String INTEGER_THRESHOLD_ERROR_MESSAGE = "Il valore di soglia deve essere intero";

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldValidateThresholdFields() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(EServiceCreationPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<EServiceCreationPage>> scenarios() {
        return Stream.of(
                new WebScenario<>(
                        "soglia per fruitore con valore decimale (virgola)",
                        fillGeneralDataThen(page ->
                                {
                                    page.thresholdAndAttributeStep().dailyCallsPerConsumer().fill("1,5");
                                    page.saveDraftButton().click();
                                }
                        ),
                        page -> Assertions.assertThat(
                                page.thresholdAndAttributeStep().getDailyCallsPerConsumerErrorText()
                        ).isEqualTo(INTEGER_THRESHOLD_ERROR_MESSAGE)
                ),

                new WebScenario<>(
                        "soglia per fruitore con valore decimale (punto)",
                        fillGeneralDataThen(page ->
                                {
                                    page.thresholdAndAttributeStep().dailyCallsPerConsumer().fill("1.5");
                                    page.saveDraftButton().click();
                                }
                        ),
                        page -> Assertions.assertThat(
                                page.thresholdAndAttributeStep().getDailyCallsPerConsumerErrorText()
                        ).isEqualTo(INTEGER_THRESHOLD_ERROR_MESSAGE)
                ),

                new WebScenario<>(
                        "soglia totale con valore decimale (virgola)",
                        fillGeneralDataThen(page ->
                                {
                                    page.thresholdAndAttributeStep().dailyCallsTotal().fill("1,5");
                                    page.saveDraftButton().click();
                                }
                        ),
                        page -> Assertions.assertThat(
                                page.thresholdAndAttributeStep().getDailyCallsTotalErrorText()
                        ).isEqualTo(INTEGER_THRESHOLD_ERROR_MESSAGE)
                ),

                new WebScenario<>(
                        "soglia totale con valore decimale (punto)",
                        fillGeneralDataThen(page ->
                                {
                                    page.thresholdAndAttributeStep().dailyCallsTotal().fill("1.5");
                                    page.saveDraftButton().click();
                                }
                        ),
                        page -> Assertions.assertThat(
                                page.thresholdAndAttributeStep().getDailyCallsTotalErrorText()
                        ).isEqualTo(INTEGER_THRESHOLD_ERROR_MESSAGE)
                )
        );
    }

    private Consumer<EServiceCreationPage> fillGeneralDataThen(Consumer<EServiceCreationPage> thresholdAction) {
        return page -> {
            fillValidGeneralData(page);
            thresholdAction.accept(page);
            page.saveDraftButton().click();
        };
    }

    private void fillValidGeneralData(EServiceCreationPage page) {
        WebEServiceGeneralData model = WebEServiceGeneralData.buildDefault();

        page.generalDataStep()
                .setName(model.eservice().getName())
                .setDescription(model.eservice().getDescription())
                .setAsyncExchange(model.eservice().getAsyncExchange())
                .setTechnology(model.eservice().getTechnology())
                .setMode(model.eservice().getMode())
                .setPersonalData(model.eservice().getPersonalData());
    }
}

