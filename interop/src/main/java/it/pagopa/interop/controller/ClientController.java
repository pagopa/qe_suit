package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.enums.Tenant;

public class ClientController {

    @Given("un client {clientType} creato da {tenant}, associato alla finalità, in cui è presente l'admin e una coppia di chiavi crittografiche")
    public void setupClient(InteropClientType clientType, Tenant consumer) {
    }
}
