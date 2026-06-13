package it.pagopa.interop.web.service;

import it.pagopa.interop.common.enums.InteropClientType;
import it.pagopa.interop.common.client.Client;
import it.pagopa.interop.common.client_assertion.ClientAssertion;
import it.pagopa.interop.common.dev_tools.VoucherRequestValidationResult;
import it.pagopa.interop.common.dpop.DPoPProof;
import it.pagopa.interop.web.page.dev_tools.debug_client_assertion.DebugClientAssertionPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevToolsService {

    @Value("${interop.auth.client-assertion.grant_type}")
    private String clientAssertionGrantType;

    @Value("${interop.auth.client-assertion.assertion_type}")
    private String clientAssertionType;

    private final DebugClientAssertionPage debugPage;

    public VoucherRequestValidationResult performValidation(ClientAssertion clientAssertion, Client client, DPoPProof proof) {
        return performValidationInternal(clientAssertion.getClientAssertion(), InteropClientType.valueOf(client.getKind().name()), client.getId().toString(), proof != null ? proof.getJwt() : null);
    }

    public VoucherRequestValidationResult performValidation(String clientAssertion, InteropClientType clientType, String clientId, String proof) {
        return performValidationInternal(clientAssertion, clientType, clientId, proof);
    }

    public void submitValidationRequest(String clientAssertion, String clientId, String dPoPProof) {
        if (clientAssertion != null) debugPage.setClientAssertion(clientAssertion);
        if (clientId != null) debugPage.setClientId(clientId);
        if (dPoPProof != null) debugPage.setDpopProof(dPoPProof);

        debugPage.submitForm();
    }

    public String getClientAssertionInputErrorMessage(){
        return debugPage.getClientAssertionErrorMessage();
    }

    private VoucherRequestValidationResult performValidationInternal(String rawClientAssertion, InteropClientType clientType, String clientId, String dPoPProof) {

        // 1. Popola la form e la sottomette
        submitValidationRequest(rawClientAssertion, clientId, dPoPProof);

        // 2. Verifica la coerenza degli Input (Echo) verificando che ciò che è stato inserito coincide con quanto specificato
        verifyRequestEcho(rawClientAssertion, clientId, dPoPProof);

        // 3. Costruisce il risultato della validazione
        VoucherRequestValidationResult result = buildValidationResult(rawClientAssertion, clientType, dPoPProof != null);

        // 4. Verifica che il risultato calcolato sia coerente con il banner di riepilogo
        if(debugPage.resultAlert().isSuccess() != result.isAllPassed())
            throw new IllegalStateException("Il risultato complessivo della validazione non è coerente con lo stato del banner di riepilogo");

        return result;
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

    private VoucherRequestValidationResult buildValidationResult(String rawClientAssertion, InteropClientType clientType, boolean hasDPoP) {
        var results = debugPage.debugResults();
        results.assertLoaded();

        ClientAssertion clientAssertion = new ClientAssertion(rawClientAssertion);
        var caValidation = results.getClientAssertionValidation();
        var pkValidation = results.getPublicKeyValidation();
        var sigValidation = results.getSignatureValidation();

        // Come indicato da https://pagopa.atlassian.net/browse/PIN-10056 si intende validare lo stato della piattaforma solo quando:
        // - Il client è tipo CONSUMER
        // - La fase di client assertion è PASSED
        var platformValidation = shouldValidatePlatform(clientType, caValidation, pkValidation) ? results.getPlatformValidation() : null;
        var dPoPValidation = hasDPoP ? results.getDPoPValidation() : null;

        return new VoucherRequestValidationResult(clientAssertion, caValidation, pkValidation, sigValidation, platformValidation, dPoPValidation);
    }

    private boolean shouldValidatePlatform(InteropClientType type, VoucherRequestValidationResult.ValidationResult caStep, VoucherRequestValidationResult.PublicKeyValidation pkStep) {
        return type == InteropClientType.CONSUMER && caStep.isSuccess() && pkStep.isSuccess();
    }
}
