package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.services.eservice.EserviceService;
import it.pagopa.interop.infrastructure.client.auth.context.user.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceController {

    private final EserviceService service;
    private final CurrentUserContext currentUserContext;

    @Given("un eservice creato da {tenant} con una richiesta di fruizione e una finalità associate da {tenant}")
    public void setupEservice(Tenant producer, Tenant consumer) {
        User producerAdmin = User.getTenantAdmin(producer);
        currentUserContext.set(producerAdmin, producer);
        service.createEservice();

        User consumerAdmin = User.getTenantAdmin(consumer);
        currentUserContext.set(consumerAdmin, consumer);
       //TODO: richiesta di fruizione e finalità
    }
}
