package it.pagopa.interop.domain.services.client_assertion.impl;

import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.model.DPoPProof;
import it.pagopa.interop.domain.services.client_assertion.DevToolsService;
import it.pagopa.interop.domain.web.pages.dev_tools.debug_client_assertion.DebugClientAssertionPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebDevToolsService implements DevToolsService {

    @Value("${interop.auth.client-assertion.grant_type}")
    private String clientAssertionGrantType;

    @Value("${interop.auth.client-assertion.assertion_type}")
    private String clientAssertionType;

    private final DebugClientAssertionPage debugPage;


    @Override
    public ClientAssertionValidationResult performValidation(ClientAssertion clientAssertion, Client client) {
        return performValidationInternal(clientAssertion.getClientAssertion(), InteropClientType.valueOf(client.getKind().name()), client.getId().toString(), null);
    }

    @Override
    public ClientAssertionValidationResult performValidation(ClientAssertion clientAssertion, Client client, DPoPProof proof) {
        return performValidationInternal(clientAssertion.getClientAssertion(), InteropClientType.valueOf(client.getKind().name()), client.getId().toString(), proof.getJwt());
    }

    @Override
    public ClientAssertionValidationResult performValidation(String clientAssertion, InteropClientType clientType, String clientId, String proof) {
        return performValidationInternal(clientAssertion, clientType, clientId, proof);
    }

    @Override
    public void submitValidationRequest(String clientAssertion, String clientId, String dPoPProof) {
        if (clientAssertion != null) debugPage.setClientAssertion(clientAssertion);
        if (clientId != null) debugPage.setClientId(clientId);
        if (dPoPProof != null) debugPage.setDpopProof(dPoPProof);

        debugPage.submitForm();
    }

    public String getClientAssertionInputErrorMessage(){
        return debugPage.getClientAssertionErrorMessage();
    }

    private ClientAssertionValidationResult performValidationInternal(String clientAssertion, InteropClientType clientType, String clientId, String dPoPProof) {

        // 1. Popola la form e la sottomette
        submitValidationRequest(clientAssertion, clientId, dPoPProof);

        // 2. Verifica coerenza Input (Echo)
        verifyRequestEcho(clientAssertion, clientId, dPoPProof);

        // 3. Costruisce il risultato della validazione
        ClientAssertionValidationResult result = buildValidationResult(clientType, dPoPProof != null);

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

    private ClientAssertionValidationResult buildValidationResult(InteropClientType clientType, boolean isDpop) {
        var results = debugPage.debugResults();
        results.assertLoaded();

        var caValidation = results.getClientAssertionValidation();
        var pkValidation = results.getPublicKeyValidation();
        var sigValidation = results.getSignatureValidation();

        // Come indicato da https://pagopa.atlassian.net/browse/PIN-10056 si intende validare lo stato della piattaforma solo quando:
        // - Il client è tipo CONSUMER
        // - La fase di client assertion è PASSED
        var platform = shouldValidatePlatform(clientType, caValidation, pkValidation) ? results.getPlatformValidation() : null;
        var dpop = isDpop ? results.getDPoPValidation() : null;

        return new ClientAssertionValidationResult(caValidation, pkValidation, sigValidation, platform, dpop);
    }

    private boolean shouldValidatePlatform(InteropClientType type, ClientAssertionValidationResult.ValidationResult caStep, ClientAssertionValidationResult.PublicKeyValidation pkStep) {
        return type == InteropClientType.CONSUMER && caStep.isSuccess() && pkStep.isSuccess();
    }
}
