package it.pagopa.interop.domain.services.client_assertion.impl;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.model.DPoPProof;
import it.pagopa.interop.domain.web.pages.dev_tools.DebugClientAssertionPage;
import it.pagopa.interop.domain.services.client_assertion.DevToolsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebDevToolsService implements DevToolsService {

    private final WebPresentationGateway webPresentationGateway;


    @Override
    public ClientAssertionValidationResult validate(ClientAssertion clientAssertion, Client client) {
        return internalValidate(clientAssertion.getClientAssertion(), InteropClientType.valueOf(client.getKind().name()), client.getId().toString(), null);
    }

    @Override
    public ClientAssertionValidationResult validate(ClientAssertion clientAssertion, Client client, DPoPProof proof) {
        return internalValidate(clientAssertion.getClientAssertion(), InteropClientType.valueOf(client.getKind().name()), client.getId().toString(), proof.getJwt());
    }

    @Override
    public ClientAssertionValidationResult validate(String clientAssertion, InteropClientType clientType, String client, String proof) {
        return internalValidate(clientAssertion,clientType,  client, proof);
    }


    private ClientAssertionValidationResult internalValidate(String clientAssertion, InteropClientType clientType, String clientId, String dPoPProof) {
        DebugClientAssertionPage debugClientAssertionPage = webPresentationGateway.bind(DebugClientAssertionPage.class);

        if(clientAssertion != null)
            debugClientAssertionPage.setClientAssertion(clientAssertion);

        if(clientId != null)
            debugClientAssertionPage.setClientId(clientId);

        //TODO: dpop proof

        return debugClientAssertionPage.validate(clientType);
    }
}
