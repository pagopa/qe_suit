package it.pagopa.interop.common.contract.template.action.strategy;

import org.assertj.core.api.Assertions;
import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface AssertionStrategy<T> {

    void assertThat(ResponseEntity<? extends T> response);

    AssertionStrategy<Object> STATUS_200 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(200);
    AssertionStrategy<Object> STATUS_201 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(201);
    AssertionStrategy<Object> STATUS_204 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(204);
    AssertionStrategy<Object> STATUS_400 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(400);
    AssertionStrategy<Object> STATUS_401 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(401);
    AssertionStrategy<Object> STATUS_403 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(403);
    AssertionStrategy<Object> STATUS_404 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(404);
    AssertionStrategy<Object> STATUS_500 = response -> Assertions.assertThat(response.getStatusCode().value()).isEqualTo(500);

}
