package it.pagopa.interop.bff.service.action;

import it.pagopa.interop.bff.service.action.strategy.AssertionStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AssertAction implements Finalizer {

    <Entity> AssertAction handle(ResponseEntity<Entity> response, AssertionStrategy<? super Entity> strategy) {
        strategy.assertThat(response);
        return this;
    }

}