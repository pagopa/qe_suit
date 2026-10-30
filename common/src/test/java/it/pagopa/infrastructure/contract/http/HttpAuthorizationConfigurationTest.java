package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Errori di configurazione della modalita' autorizzazione: emergono alla dichiarazione, non
 * durante l'esecuzione, e sono sempre {@link ContractHttpException} — mai esiti HTTP.
 */
class HttpAuthorizationConfigurationTest {

    private final HttpContractValidator contract = new HttpContractValidator(
            new ObjectMapper(),
            source -> List.of(),
            source -> List.of(),
            source -> {
                throw new AssertionError("non deve essere invocato");
            },
            completePolicy()
    );

    /**
     * Il costruttore di {@link HttpContractValidator} pretende una policy completa per il percorso
     * di fuzzing, anche quando si usa solo la modalita' autorizzazione (che non la consulta).
     */
    private static HttpContractPolicy completePolicy() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().success(response -> {
        });
        for (it.pagopa.infrastructure.fuzzing.FuzzScenario scenario
                : it.pagopa.infrastructure.fuzzing.FuzzScenario.values()) {
            builder.scenario(scenario, response -> {
            });
        }
        return builder.build();
    }

    private static final Supplier<?> ANY_CALL = Object::new;

    @Test
    void nullIdentitiesIsRejected() {
        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(null)
        );
        assertTrue(exception.getMessage().contains("must not be null"));
    }

    /**
     * Una dichiarazione senza utenze non proverebbe nulla: va trattata come errore, non come
     * suite vuota che passa silenziosamente.
     */
    @Test
    void emptyIdentitiesIsRejected() {
        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(List.of())
        );
        assertTrue(exception.getMessage().contains("must not be empty"));
    }

    @Test
    void nullIdentityElementIsRejectedWithItsIndex() {
        List<HttpContractIdentity> withNull = new ArrayList<>();
        withNull.add(new HttpContractIdentity("USER_1", () -> {
        }));
        withNull.add(null);

        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(withNull)
        );
        assertTrue(exception.getMessage().contains("index 1"));
    }

    @Test
    void blankIdentityNameAndNullAuthenticationAreRejected() {
        assertThrows(ContractHttpException.class, () -> new HttpContractIdentity(null, () -> {
        }));
        assertThrows(ContractHttpException.class, () -> new HttpContractIdentity("  ", () -> {
        }));
        assertThrows(ContractHttpException.class, () -> new HttpContractIdentity("USER_1", null));
    }

    @Test
    void emptyOrNullApiCallsIsRejected() {
        assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(identities()).apiCalls()
        );
        assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(identities()).apiCalls((Supplier<?>[]) null)
        );
    }

    @Test
    void nullApiCallSupplierIsRejectedWithItsIndex() {
        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(identities()).apiCalls(ANY_CALL, null)
        );
        assertTrue(exception.getMessage().contains("index 1"));
    }

    @Test
    void invalidExpectedStatusIsRejected() {
        assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(identities()).apiCalls(ANY_CALL).expectedStatus(0)
        );
        assertThrows(
                ContractHttpException.class,
                () -> contract.authorization(identities()).apiCalls(ANY_CALL).expectedStatus(600)
        );
    }

    @Test
    void lastExpectedStatusWins() {
        assertEquals(
                1,
                contract.authorization(identities())
                        .apiCalls(ANY_CALL)
                        .expectedStatus(401)
                        .expectedStatus(403)
                        .tests()
                        .count()
        );
    }

    @Test
    void testsBeforeApiCallsIsRejected() {
        HttpContractStages.AuthorizationApiCallsStage stage = contract.authorization(identities());

        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> ((HttpContractStages.AuthorizationTestsStage) stage).tests()
        );
        assertTrue(exception.getMessage().contains("apiCalls"));
    }

    private static List<HttpContractIdentity> identities() {
        return List.of(new HttpContractIdentity("USER_1", () -> {
        }));
    }
}
