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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebDevToolsService implements DevToolsService {

    private final DebugClientAssertionPage debugPage;


    @Override
    public ClientAssertionValidationResult validate(ClientAssertion clientAssertion, Client client) {
        return internalValidate(clientAssertion.getClientAssertion(), InteropClientType.valueOf(client.getKind().name()), client.getId().toString(), null);
    }

    @Override
    public ClientAssertionValidationResult validate(ClientAssertion clientAssertion, Client client, DPoPProof proof) {
        return internalValidate(clientAssertion.getClientAssertion(), InteropClientType.valueOf(client.getKind().name()), client.getId().toString(), proof.getJwt());
    }

    @Override
    public ClientAssertionValidationResult validate(String clientAssertion, InteropClientType clientType, String clientId, String proof) {
        return internalValidate(clientAssertion, clientType, clientId, proof);
    }

    @Override
    public void submitForm(String clientAssertion, String clientId, String dPoPProof) {
        if (clientAssertion != null) debugPage.setClientAssertion(clientAssertion);
        if (clientId != null) debugPage.setClientId(clientId);
        if (dPoPProof != null) debugPage.setDpopProof(dPoPProof);

        debugPage.submitForm();
    }

    private ClientAssertionValidationResult internalValidate(String clientAssertion, InteropClientType clientType, String clientId, String dPoPProof) {
        submitForm(clientAssertion, clientId, dPoPProof);

        debugPage.debugResults().assertLoaded();
        var results = debugPage.debugResults();

        var clientAssertionValidation = results.getClientAssertionValidation();
        var publicKeyValidation = results.getPublicKeyValidation();
        var signatureValidation = results.getSignatureValidation();

        // Come indicato da https://pagopa.atlassian.net/browse/PIN-10056 si intende validare lo stato della piattaforma solo quando:
        // - Il client è tipo CONSUMER
        // - La fase di client assertion è PASSED
        ClientAssertionValidationResult.PlatformValidation platformValidation = null;
        if (clientType == InteropClientType.CONSUMER && clientAssertionValidation.isSuccess())
            platformValidation = results.getPlatformValidation();


        // DPoP validation solo se presente la proof
        ClientAssertionValidationResult.DPoPValidation dPoPValidation = null;
        if (dPoPProof != null)
            dPoPValidation = results.getDPoPValidation();


        return new ClientAssertionValidationResult(
                clientAssertionValidation,
                publicKeyValidation,
                signatureValidation,
                platformValidation,
                dPoPValidation
        );
    }
}
