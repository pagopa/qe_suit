package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.contract.service.IEServiceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceController {

    private final IEServiceTestService service;

    @Given("un EService in stato DRAFT avente tutti i campi compilati, tra cui:")
    public void createWithOverride(Map<String, String> rawSeed) {
        service
            .createDraftWithOverride(rawSeed)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .andUpdateContext();
    }

    @Given("un EService in stato DRAFT con:")
    public void createWith(Map<String, String> rawSeed) {
        service
            .createDraftWith(rawSeed)
            .withPolling(PollingStrategy.UNTIL_SUCCESS)
            .andUpdateContext();
    }
}