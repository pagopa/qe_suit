package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementListPage;
import it.pagopa.interop.web.agreement.infrastructure.page.ProducerAgreementDetailPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerAgreementContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> shouldShowReceivedAgreementListToProducer() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(ProducerAgreementListPage.class)
                        .tests(Stream.of(new WebScenario<>(
                                "richieste di fruizione ricevute accessibili all'utente " + role.name() + " del Comune di Milano",
                                page -> {},
                                page -> {}
                        ))));
    }

    @TestFactory
    Stream<DynamicTest> shouldShowAgreementDetailToProducer() {
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        Agreement agreement = interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .get(Agreement.class);

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(producer, role), producer)
                        .on(ProducerAgreementDetailPage.class, agreement.getId().toString())
                        .tests(Stream.of(new WebScenario<>(
                                "richiesta di fruizione ricevuta visibile all'utente " + role.name() + " del Comune di Milano",
                                page -> {},
                                page -> {}
                        ))));
    }
}
