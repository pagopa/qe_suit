package it.pagopa.interop.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.enums.Tenant;

public class EserviceController {
    @Given("un eservice creato da {tenant} con una richiesta di fruizione e una finalità associate da {tenant}")
    public void setupEservice(Tenant producer, Tenant consumer) {
    }
}
