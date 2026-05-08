package it.pagopa.interop.domain.services.client_assertion.impl;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import it.pagopa.interop.domain.web.pages.dev_tools.DebugClientAssertionPage;
import it.pagopa.interop.domain.services.client_assertion.ClientAssertionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebClientAssertionService implements ClientAssertionService {

    private final WebPresentationGateway webPresentationGateway;

    @Override
    public ClientAssertionValidationResult validateClientAssertion(ClientAssertion clientAssertion, Client client) {
        DebugClientAssertionPage debugClientAssertionPage = webPresentationGateway.bind(DebugClientAssertionPage.class);
        debugClientAssertionPage.setClientAssertion(clientAssertion.getClientAssertion());
        debugClientAssertionPage.setClientId(client.getId().toString());

        return debugClientAssertionPage.validate();
    }
}
