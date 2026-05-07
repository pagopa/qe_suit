package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.model.Agreement;
import it.pagopa.interop.domain.model.Eservice;
import it.pagopa.interop.domain.services.agreement.AgreementService;
import it.pagopa.interop.domain.services.eservice.EserviceService;
import it.pagopa.interop.domain.services.purpose.PurposeService;
import it.pagopa.interop.infrastructure.client.auth.context.user.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceController {

    private final EserviceService service;
    private final AgreementService agreementService;
    private final CurrentUserContext currentUserContext;
    private final PurposeService purposeService;

    @Given("un eservice creato da {tenant} con una richiesta di fruizione e una finalità associate da {tenant}")
    public void setupEservice(Tenant producer, Tenant consumer) {
       Eservice draftEservice = createDraftEservice(producer);
       Eservice publishedEservice = publishEservice(producer, draftEservice);

       currentUserContext.set(User.getTenantAdmin(consumer), consumer);
       Agreement draftAgreement = agreementService.createEserviceAgreement(publishedEservice);
       Agreement activeAgreement = agreementService.publishAgreement(draftAgreement);

       //TODO: finalità
    }

    public Eservice createDraftEservice(Tenant producer) {
        User producerAdmin = User.getTenantAdmin(producer);
        currentUserContext.set(producerAdmin, producer);
        return service.createEservice();
    }

    public Eservice publishEservice(Tenant producer, Eservice draftEservice) {
        User producerAdmin = User.getTenantAdmin(producer);
        currentUserContext.set(producerAdmin, producer);
        return service.publishEservice(draftEservice);
    }
}
