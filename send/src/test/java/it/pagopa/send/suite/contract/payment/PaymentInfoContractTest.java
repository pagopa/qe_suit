package it.pagopa.send.suite.contract.payment;

import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.TargetExpression;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.externalregistries.payment.ApiClient;
import it.pagopa.send.payment.infrastructure.PaymentInfoRequestFactory;
import it.pagopa.send.payment.infrastructure.PaymentInfoRequestFactory.PaymentInfoRequests;
import it.pagopa.send.payment.infrastructure.config.PaymentInfoApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static it.pagopa.send.payment.infrastructure.config.PaymentInfoApiContractConfig.SUCCESS_STATUS_CODE;

/**
 * Contract test (fuzzing) di {@code pn-payment-info-external-v1.yaml} (pn-external-registries).
 * <p>
 * Le operazioni non sono piu' pubblicate su API Gateway (PN-13569): il test le chiama sull'ALB interno attraverso
 * il tunnel SSM ({@code pn tunnel up --env <ENV>}, porta 8886), senza authorizer e senza WAF. L'identita'
 * dichiarata in {@code as(...)} serve solo a creare gli avvisi pagoPA intestati al destinatario PF.
 * <p>
 * Checklist delle operazioni coperte:
 * <ol>
 *   <li>{@code getPaymentInfo} (deprecated) GET  /ext-registry/pagopa/v1/paymentinfo/{paTaxId}/{noticeNumber} pathParams 200</li>
 *   <li>{@code getPaymentInfoV21}           POST /ext-registry/pagopa/v2.1/paymentinfo                         payload    200</li>
 *   <li>{@code checkoutCart}                POST /ext-registry/pagopa/v1/checkout-cart                         payload    200</li>
 * </ol>
 * Origine degli attesi:
 * <ul>
 *   <li>ogni mutazione segue la policy di {@link PaymentInfoApiContractConfig} (400), come nelle altre suite di
 *       fuzzing: i modelli generati non portano vincoli di validazione, quindi il framework non deduce alcun
 *       successo, e per le injection la validita' resta comunque sconosciuta;</li>
 *   <li>l'unica eccezione e' la rimozione di un elemento dal body v2.1, che lo spec rende valida
 *       ({@code minItems: 1});</li>
 *   <li>non si deducono successi dall'assenza di vincoli nello spec: testo libero ({@code companyName},
 *       {@code description}), {@code returnUrl} non URL, importo massimo o decimale e path vuoti restano alla
 *       policy finche' il team non definisce l'ammissione di quegli input.</li>
 * </ul>
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, PaymentInfoApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PaymentInfoContractTest {

    private static final UserAlias DEBTOR = UserAlias.USER_1;

    private final ApiClient apiClient;
    private final SendHttpContractValidator httpContractValidator;
    private final UserProvider userProvider;
    private final PaymentInfoRequestFactory requestFactory;

    public PaymentInfoContractTest(
            @Qualifier("paymentInfoApiClient") ApiClient apiClient,
            @Qualifier("paymentInfoHttpContractValidator") SendHttpContractValidator httpContractValidator,
            UserProvider userProvider,
            PaymentInfoRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.httpContractValidator = httpContractValidator;
        this.userProvider = userProvider;
        this.requestFactory = requestFactory;
    }

    @TestFactory
    Stream<DynamicTest> getPaymentInfo() {
        return httpContractValidator
                .as(DEBTOR)
                .apiCall(() -> apiClient.paymentInfo().getPaymentInfo())
                .pathParams(() -> requestFactory.paymentInfoPathParams(debtor()))
                .tests();
    }

    /**
     * Rimuovere uno dei due elementi lascia una lista valida ({@code minItems: 1}). I campi interni agli
     * elementi restano alla policy: il framework non risolve target su campi degli elementi di una lista radice.
     */
    @TestFactory
    Stream<DynamicTest> getPaymentInfoV21() {
        List<TargetExpression<PaymentInfoRequests>> items = List.of(
                requests -> requests.get(0),
                requests -> requests.get(1)
        );
        return httpContractValidator
                .as(DEBTOR)
                .apiCall(() -> apiClient.paymentInfo().getPaymentInfoV21())
                .payload(() -> requestFactory.paymentInfoRequests(debtor()))
                .targets(FuzzScenario.REMOVED, expectStatus(SUCCESS_STATUS_CODE), items)
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> checkoutCart() {
        return httpContractValidator
                .as(DEBTOR)
                .apiCall(() -> apiClient.paymentInfo().checkoutCart())
                .payload(() -> requestFactory.checkoutRequest(debtor()))
                .tests();
    }

    private User debtor() {
        return userProvider.resolve(DEBTOR);
    }

    private static Consumer<Response> expectStatus(int status) {
        return response -> response.then().statusCode(status);
    }
}
