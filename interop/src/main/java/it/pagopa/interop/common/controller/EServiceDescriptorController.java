package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.bff.journey.TestJourney;
import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptorState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EServiceDescriptorController {

    private final TestJourney journey;

    @Given("un EService in stato {eServiceDescriptorState}")
    public void createEService(EServiceDescriptorState state) {
        journey.createEService(state);
    }
}
