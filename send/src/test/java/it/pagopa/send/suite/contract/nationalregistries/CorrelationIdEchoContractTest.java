package it.pagopa.send.suite.contract.nationalregistries;

import io.restassured.response.Response;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.generated.openapi.clients.nationalregistries.ApiClient;
import it.pagopa.send.generated.openapi.clients.nationalregistries.model.*;
import it.pagopa.send.nationalregistries.infrastructure.NationalRegistriesRequestFactory;
import it.pagopa.send.nationalregistries.infrastructure.config.NationalRegistriesApiContractConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * [QA-18856] (seguito): verifica che il {@code correlationId} inviato nel body torni invariato
 * nell'header {@code X-Request-ID} della risposta, per ognuna delle 11 operazioni che oggi
 * accettano il campo (3 gia' required, 8 rese opzionali da PN-22162). Non e' un test di fuzzing:
 * e' un solo caso "happy path" per operazione, percio' chiama l'{@code ApiClient} direttamente
 * invece di passare dalla DSL di {@code HttpContractValidator}.
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, NationalRegistriesApiContractConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CorrelationIdEchoContractTest {

    private final ApiClient apiClient;
    private final NationalRegistriesRequestFactory requestFactory;
    private final CurrentUserSession currentUserSession;
    private final UserProvider userProvider;

    public CorrelationIdEchoContractTest(
            @Qualifier("nationalRegistriesApiClient") ApiClient apiClient,
            NationalRegistriesRequestFactory requestFactory,
            CurrentUserSession currentUserSession,
            UserProvider userProvider
    ) {
        this.apiClient = apiClient;
        this.requestFactory = requestFactory;
        this.currentUserSession = currentUserSession;
        this.userProvider = userProvider;
    }

    @BeforeEach
    void setUser() {
        currentUserSession.setUser(userProvider.resolve(UserAlias.COMUNE_1));
    }

    @Test
    void ipaPec() {
        IPARequestBody payload = requestFactory.ipaRequestBody();
        Response response = apiClient.ipa().ipaPec().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void adeLegal() {
        ADELegalRequestBody payload = requestFactory.adeLegalRequestBody();
        Response response = apiClient.agenziaEntrate().adeLegal().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void infoCamereLegal() {
        InfoCamereLegalRequestBody payload = requestFactory.infoCamereLegalRequestBody();
        Response response = apiClient.infoCamere().infoCamereLegal().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void infoCamereLegalInstitutions() {
        InfoCamereLegalInstitutionsRequestBody payload = requestFactory.infoCamereLegalInstitutionsRequestBody();
        Response response = apiClient.infoCamere().infoCamereLegalInstitutions().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void checkTaxId() {
        CheckTaxIdRequestBody payload = requestFactory.checkTaxIdRequestBody();
        Response response = apiClient.agenziaEntrate().checkTaxId().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void addressANPR() {
        GetAddressANPRRequestBody payload = requestFactory.addressANPRRequestBody();
        Response response = apiClient.addressAnpr().addressANPR().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void digitalAddressINAD() {
        GetDigitalAddressINADRequestBody payload = requestFactory.digitalAddressINADRequestBody();
        Response response = apiClient.digitalAddressInad().digitalAddressINAD().recipientTypePath("PF").body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void addressRegistroImprese() {
        GetAddressRegistroImpreseRequestBody payload = requestFactory.addressRegistroImpreseRequestBody();
        Response response = apiClient.infoCamere().addressRegistroImprese().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
    }

    @Test
    void getAddresses() {
        AddressRequestBody payload = requestFactory.addressRequestBody();
        Response response = apiClient.address().getAddresses().recipientTypePath("PF").body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
        // AddressOK riporta anche correlationId nel body: lo yaml lo prevedeva gia' prima di PN-22162.
        assertEquals(payload.getFilter().getCorrelationId(), response.as(AddressOK.class).getCorrelationId());
    }

    @Test
    void getPhysicalAddresses() {
        PhysicalAddressesRequestBody payload = requestFactory.physicalAddressesRequestBody();
        Response response = apiClient.address().getPhysicalAddresses().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getCorrelationId(), response.header("X-Request-ID"));
        assertEquals(payload.getCorrelationId(), response.as(PhysicalAddressesResponse.class).getCorrelationId());
    }

    @Test
    void digitalAddressIniPEC() {
        GetDigitalAddressIniPECRequestBody payload = requestFactory.digitalAddressIniPECRequestBody();
        Response response = apiClient.infoCamere().digitalAddressIniPEC().body(payload)
                .execute(r -> r.then().statusCode(200).extract().response());
        assertEquals(payload.getFilter().getCorrelationId(), response.header("X-Request-ID"));
        assertEquals(payload.getFilter().getCorrelationId(), response.as(GetDigitalAddressIniPECOK.class).getCorrelationId());
    }
}
