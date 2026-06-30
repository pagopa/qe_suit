package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.service.EServiceService;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceCommonController {

    private final EServiceService<?> service;

    @Given("un EService generato compilando tutti i campi")
    public void create() {
        service
            .create()
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .andUpdateContext();
    }

    @Given("un EService in stato DRAFT avente tutti i campi compilati, tra cui:")
    public void createWithOverride(Map<String, String> rawSeed) {
        service
            .createWithOverride(rawSeed)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .andUpdateContext();
    }

    @Given("un EService in stato DRAFT con:")
    public void createWith(Map<String, String> rawSeed) {
        service
            .createWith(rawSeed)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .andUpdateContext();
    }
}