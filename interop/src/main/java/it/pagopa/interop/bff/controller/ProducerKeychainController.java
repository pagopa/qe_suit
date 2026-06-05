package it.pagopa.interop.bff.controller;

import it.pagopa.interop.bff.service.ProducerKeychainService;
import it.pagopa.interop.bff.service.action.strategy.AssertionStrategy;
import it.pagopa.interop.bff.service.action.strategy.PollingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainController {
    private final ProducerKeychainService producerKeychainService;


    public void createKeychain() {
        producerKeychainService
                .create()
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .andAssert(AssertionStrategy.STATUS_200);

        producerKeychainService
                .create()
                .withPolling((statusCode, resource) ->
                        resource.getId().equals(UUID.randomUUID())
                )
                .andAssert(response ->
                        response.getStatusCode().is2xxSuccessful()
                );
    }

}
