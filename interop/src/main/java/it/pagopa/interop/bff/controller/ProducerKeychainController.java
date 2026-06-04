package it.pagopa.interop.bff.controller;

import it.pagopa.interop.bff.service.ProducerKeychainService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainController {
    private final ProducerKeychainService producerKeychainService;


}
