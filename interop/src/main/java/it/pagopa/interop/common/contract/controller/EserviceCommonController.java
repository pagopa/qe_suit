package it.pagopa.interop.common.contract.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.service.EServiceService;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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