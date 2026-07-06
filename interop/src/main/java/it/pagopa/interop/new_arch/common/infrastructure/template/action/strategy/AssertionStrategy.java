package it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import org.assertj.core.api.Assertions;

@FunctionalInterface
public interface AssertionStrategy {

    void assertThat(ApiResponse response);

    AssertionStrategy STATUS_200 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(200);
    AssertionStrategy STATUS_201 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(201);
    AssertionStrategy STATUS_204 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(204);
    AssertionStrategy STATUS_400 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(400);
    AssertionStrategy STATUS_401 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(401);
    AssertionStrategy STATUS_403 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(403);
    AssertionStrategy STATUS_404 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(404);
    AssertionStrategy STATUS_500 = response -> Assertions.assertThat(response.statusCode()).isEqualTo(500);

}
