package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.dto.Client;
import it.pagopa.interop.domain.dto.ClientAssertion;
import it.pagopa.interop.domain.dto.Purpose;
import it.pagopa.interop.domain.enums.User;

public class ClientAssertionController {
    @Given("una client assertion valida generata usando il client e la finalità")
    public void createClientAssertion(Client client, Purpose purpose) {
    }

    @Given("{currentUser} richiede la validazione della {currentClientAssertion}")
    public void validateClientAssertion(User currentUser, ClientAssertion clientAssertion){

    }

    @Given("i risultati della validazione sono:")
    public void checkValidationResult(){

    }
}
