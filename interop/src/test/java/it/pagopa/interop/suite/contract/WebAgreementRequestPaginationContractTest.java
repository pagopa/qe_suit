package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.agreement.infrastructure.page.AgreementRequestPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {
                "spring.profiles.include=junit",
                "channel.web.browser=chrome",
                "channel.web.headless=false",
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAgreementRequestPaginationContractTest {

    private static final Tenant PRODUCER = Tenant.COMUNE_DI_MILANO;
    private static final Tenant CONSUMER = Tenant.COMUNE_DI_POZZALLO;

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> shouldKeepPaginationUsableWhenOffsetExceedsRealTotal() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(CONSUMER),
                        CONSUMER
                )
                .on(AgreementRequestPage.class, "10", "0")
                .tests(scenarios());
    }

    private Stream<WebScenario<AgreementRequestPage>> scenarios() {
        return Stream.of(
                offsetBeyondRealTotalScenario(),
                offsetOnLastValidPageScenario(),
                singlePageOverflowScenario()
        );
    }

    private WebScenario<AgreementRequestPage> offsetBeyondRealTotalScenario() {
        AtomicInteger lastPageNumber = new AtomicInteger();

        return new WebScenario<>(
                "offset oltre il totale reale rimane navigabile",
                page -> {
                    createPendingAgreementRequest();
                    page.navigateTo("10", "0");

                    int n = page.table().pagination().lastPageNumber();
                    lastPageNumber.set(n);

                    page.navigateTo("10", String.valueOf(n * 10));
                },
                page -> {
                    Assertions.assertThat(page.table().pagination().isUsable()).isTrue();
                    Assertions.assertThat(page.table().pagination().lastPageNumber())
                            .isEqualTo(lastPageNumber.get());
                }
        );
    }

    private WebScenario<AgreementRequestPage> offsetOnLastValidPageScenario() {
        AtomicInteger lastPageNumber = new AtomicInteger();

        return new WebScenario<>(
                "offset = (N-1)*10 (ultima pagina valida) rimane navigabile",
                page -> {
                    createPendingAgreementRequest();
                    page.navigateTo("10", "0");

                    int n = page.table().pagination().lastPageNumber();
                    lastPageNumber.set(n);

                    page.navigateTo("10", String.valueOf((n - 1) * 10));
                },
                page -> {
                    Assertions.assertThat(page.table().pagination().isUsable()).isTrue();
                    Assertions.assertThat(page.table().pagination().lastPageNumber())
                            .isEqualTo(lastPageNumber.get());
                }
        );
    }

    private WebScenario<AgreementRequestPage> singlePageOverflowScenario() {
        return new WebScenario<>(
                "singola pagina disponibile: offset alto non produce 'nessun risultato'",
                page -> {
                    createPendingAgreementRequest();
                    page.navigateTo("10", "0");

                    // Questo scenario ha senso solo quando l'intero risultato sta in una
                    // sola pagina. In un ambiente condiviso (QA) non è possibile garantirlo
                    // in modo deterministico, quindi il caso viene saltato (non fallito)
                    // quando il precondition non produce esattamente una pagina.
                    Assumptions.assumeTrue(
                            page.table().pagination().pageButtons().size() <= 1,
                            "Skip: l'ambiente ha più di una pagina di risultati, impossibile verificare il caso N=1"
                    );

                    page.navigateTo("10", "100");
                },
                page -> {
                    Assertions.assertThat(page.table().rows()).isNotEmpty();
                    Assertions.assertThat(page.table().noResultsAlert()).isNotPresent();
                }
        );
    }

    private void createPendingAgreementRequest() {
        interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(CONSUMER, UserRole.ADMIN)
                .linkAgreement(AgreementState.PENDING);
    }
}

