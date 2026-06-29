package it.pagopa.interop.common.contract.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.contract.service.EServiceService;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class EserviceCommonController {

    private final EServiceService service;

    @Given("un EService generato compilando tutti i campi")
    public void create() {
        service
                .create()
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andUpdateContext();
    }
}