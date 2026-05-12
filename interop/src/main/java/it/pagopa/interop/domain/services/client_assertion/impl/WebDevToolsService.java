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

    private ClientAssertionValidationResult performValidationInternal(String clientAssertion, InteropClientType clientType, String clientId, String dPoPProof) {
        submitValidationRequest(clientAssertion, clientId, dPoPProof);

        // Verifico il corretto caricamento della request in interfaccia e la correttezza dei dati mostrati prima di procedere con la lettura dei risultati di validazione
        debugPage.requestContent().assertLoaded();
        var request = debugPage.requestContent();
        
        request.verifyVoucherType(dPoPProof != null ? "DPoP" : "Bearer");
        request.verifyClientId(clientId);
        request.verifyClientAssertion(clientAssertion);
        request.verifyDpopProof(dPoPProof);
        request.verifyClientAssertionType(clientAssertionType);
        request.verifyGrantType(clientAssertionGrantType);
        
        // Recupero i risultati di validazione dalla pagina e li trasformo in un oggetto ClientAssertionValidationResult
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
