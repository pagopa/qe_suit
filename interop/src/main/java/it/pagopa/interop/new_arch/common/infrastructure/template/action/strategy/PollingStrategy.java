package it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy;

import it.pagopa.interop.new_arch.common.infrastructure.response.ApiResponse;

@FunctionalInterface
public interface PollingStrategy {

    boolean isSatisfied(ApiResponse response);

    PollingStrategy UNTIL_SUCCESS = PollingStrategy::is2xxSuccessful;

    PollingStrategy UNTIL_ERROR = (response) -> !is2xxSuccessful(response);

    static boolean is2xxSuccessful(ApiResponse apiResponse) {
        return apiResponse.is2xxSuccessful();
    }
}