package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.enums.InteropClientType;

public class ClientController {

    @Given("un client {string} creato da {string}, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche")
    public void setupClient(User user, InteropClientType clientType) {
    }
}
