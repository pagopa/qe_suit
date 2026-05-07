package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;

public class ClientAssertionController {
    @Given("una client assertion valida generata usando il client e la finalità")
    public void createClientAssertion() {
    }

    @Given("{string} richiede la validazione della client assertion")
    public void validateClientAssertion(ClientAssertion clientAssertion){

    }

    @Given("i risultati della validazione sono:")
    public void checkValidationResult(){

    }
}
