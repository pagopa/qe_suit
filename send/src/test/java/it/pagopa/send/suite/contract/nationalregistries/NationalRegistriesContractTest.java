package it.pagopa.send.suite.contract.nationalregistries;

import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.infrastructure.contract.SendHttpContractValidator;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.generated.openapi.clients.nationalregistries.ApiClient;
import it.pagopa.send.generated.openapi.clients.nationalregistries.model.*;
import it.pagopa.send.nationalregistries.infrastructure.NationalRegistriesRequestFactory;
import it.pagopa.send.nationalregistries.infrastructure.config.NationalRegistriesApiContractConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * [QA-18856] (seguito): PN-22162 ha aggiunto {@code correlationId} come campo opzionale del body a
 * 8 delle 11 operazioni di PN-NationalRegistries; nelle altre 3 ({@code getAddresses},
 * {@code getPhysicalAddresses}, {@code digitalAddressIniPEC}) il campo esisteva gia' ed e'
 * rimasto required. Ogni test fissa esplicitamente l'aspettativa su
 * {@link FuzzScenario#REMOVED} per {@code correlationId}, cosi' che un futuro cambio di
 * obbligatorieta' nello yaml faccia fallire il test invece di passare inosservato.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, NationalRegistriesApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class NationalRegistriesContractTest {

    private static final Consumer<Response> OPTIONAL_FIELD_DROPPED = response -> response.then().statusCode(200);
    private static final Consumer<Response> REQUIRED_FIELD_DROPPED = response -> response.then().statusCode(400);

    private final ApiClient apiClient;
    private final SendHttpContractValidator validator;
    private final NationalRegistriesRequestFactory requestFactory;

    public NationalRegistriesContractTest(
            @Qualifier("nationalRegistriesApiClient") ApiClient apiClient,
            @Qualifier("nationalRegistriesHttpContractValidator") SendHttpContractValidator validator,
            NationalRegistriesRequestFactory requestFactory
    ) {
        this.apiClient = apiClient;
        this.validator = validator;
        this.requestFactory = requestFactory;
    }

    @TestFactory
    Stream<DynamicTest> ipaPec() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.ipa().ipaPec())
                .payload(requestFactory::ipaRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .targets(FuzzScenario.REPLACED_WITH_EMPTY_STRING, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> adeLegal() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.agenziaEntrate().adeLegal())
                .payload(requestFactory::adeLegalRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> infoCamereLegal() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.infoCamere().infoCamereLegal())
                .payload(requestFactory::infoCamereLegalRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> infoCamereLegalInstitutions() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.infoCamere().infoCamereLegalInstitutions())
                .payload(requestFactory::infoCamereLegalInstitutionsRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> checkTaxId() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.agenziaEntrate().checkTaxId())
                .payload(requestFactory::checkTaxIdRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> addressANPR() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.addressAnpr().addressANPR())
                .payload(requestFactory::addressANPRRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .targets(FuzzScenario.REPLACED_WITH_EMPTY_STRING, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> digitalAddressINAD() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.digitalAddressInad().digitalAddressINAD().recipientTypePath("PF"))
                .payload(requestFactory::digitalAddressINADRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .targets(FuzzScenario.REPLACED_WITH_EMPTY_STRING, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> addressRegistroImprese() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.infoCamere().addressRegistroImprese())
                .payload(requestFactory::addressRegistroImpreseRequestBody)
                .targets(FuzzScenario.REMOVED, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .targets(FuzzScenario.REPLACED_WITH_EMPTY_STRING, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    // Le 3 operazioni seguenti avevano correlationId gia' required prima di PN-22162: la PR non le
    // ha toccate, quindi l'aspettativa su REMOVED resta 400.

    @TestFactory
    Stream<DynamicTest> getAddresses() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.address().getAddresses().recipientTypePath("PF"))
                .payload(requestFactory::addressRequestBody)
                .targets(FuzzScenario.REMOVED, REQUIRED_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> getPhysicalAddresses() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.address().getPhysicalAddresses())
                .payload(requestFactory::physicalAddressesRequestBody)
                .targets(FuzzScenario.REMOVED, REQUIRED_FIELD_DROPPED, List.of(PhysicalAddressesRequestBody::getCorrelationId))
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> digitalAddressIniPEC() {
        return validator.as(UserAlias.COMUNE_1)
                .apiCall(() -> apiClient.infoCamere().digitalAddressIniPEC())
                .payload(requestFactory::digitalAddressIniPECRequestBody)
                .targets(FuzzScenario.REMOVED, REQUIRED_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .targets(FuzzScenario.REPLACED_WITH_EMPTY_STRING, OPTIONAL_FIELD_DROPPED, List.of(body -> body.getFilter().getCorrelationId()))
                .tests();
    }
}
