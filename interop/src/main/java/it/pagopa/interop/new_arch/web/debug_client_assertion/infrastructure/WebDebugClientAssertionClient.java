package it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure;

import it.pagopa.interop.new_arch.common.client.domain.ClientKind;
import it.pagopa.interop.new_arch.common.debug_client_assertion.domain.DebugClientAssertionValidation;
import it.pagopa.interop.new_arch.common.infrastructure.template.BrowserClient;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.new_arch.common.kernel.security.ClientAssertion;
import it.pagopa.interop.new_arch.web.debug_client_assertion.infrastructure.page.DebugClientAssertionPage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebDebugClientAssertionClient extends BrowserClient {
    @Value("${interop.auth.client-assertion.grant_type}")
    private String clientAssertionGrantType;

    @Value("${interop.auth.client-assertion.assertion_type}")
    private String clientAssertionType;

    @Getter
    private final DebugClientAssertionPage debugPage;

    public TestChain<DebugClientAssertionValidation> submitClientAssertionValidationRequest(String clientAssertion, ClientKind clientKind, String clientId, String dPoPProof) {
        return navigateAndExecute(
                debugPage,
                () -> {
                    // 1. Popola la form e la sottomette
                    // 2. Verifica la coerenza degli Input (Echo) verificando che ciò che è stato inserito coincide con quanto specificato
                    submitValidationRequest(clientAssertion, clientId, dPoPProof);

                    // 3. Costruisce il risultato della validazione
                    DebugClientAssertionValidation result = buildValidationResult(clientAssertion, clientKind, dPoPProof != null);

                    // 4. Verifica che il risultato calcolato sia coerente con il banner di riepilogo
                    if (debugPage.resultAlert().isSuccess() != result.isAllPassed())
                        throw new IllegalStateException("Il risultato complessivo della validazione non è coerente con lo stato del banner di riepilogo");

                    return result;
                },
                DebugClientAssertionValidation.class
        );
    }

    private void submitValidationRequest(String clientAssertion, String clientId, String dPoPProof) {
        debugPage.setClientAssertion(clientAssertion);
        debugPage.setClientId(clientId);
        debugPage.setDpopProof(dPoPProof);

        debugPage.submitForm();

        verifyRequestEcho(clientAssertion, clientId, dPoPProof);
    }

    private void verifyRequestEcho(String clientAssertion, String clientId, String dPoPProof) {
        var request = debugPage.requestContent();
        request.assertLoaded();

        request.verifyVoucherType(dPoPProof != null ? "DPoP" : "Bearer");
        request.verifyClientId(clientId);
        request.verifyClientAssertion(clientAssertion);
        request.verifyDpopProof(dPoPProof);
        request.verifyClientAssertionType(clientAssertionType);
        request.verifyGrantType(clientAssertionGrantType);
    }

    private DebugClientAssertionValidation buildValidationResult(String rawClientAssertion, ClientKind clientType, boolean hasDPoP) {
        var results = debugPage.debugResults();
        results.assertLoaded();

        ClientAssertion clientAssertion = ClientAssertion.builder().clientAssertion(rawClientAssertion).build();
        var caValidation = results.getClientAssertionValidation();
        var pkValidation = results.getPublicKeyValidation();
        var sigValidation = results.getSignatureValidation();

        // Come indicato da https://pagopa.atlassian.net/browse/PIN-10056 si intende validare lo stato della piattaforma solo quando:
        // - Il client è tipo CONSUMER
        // - La fase di client assertion è PASSED
        var platformValidation = shouldValidatePlatform(clientType, caValidation, pkValidation) ? results.getPlatformValidation() : null;
        var dPoPValidation = hasDPoP ? results.getDPoPValidation() : null;

        return new DebugClientAssertionValidation(clientAssertion, caValidation, pkValidation, sigValidation, platformValidation, dPoPValidation);
    }

    private boolean shouldValidatePlatform(ClientKind type, DebugClientAssertionValidation.ValidationResult caStep, DebugClientAssertionValidation.PublicKeyValidation pkStep) {
        return type == ClientKind.CONSUMER && caStep.isSuccess() && pkStep.isSuccess();
    }
}
