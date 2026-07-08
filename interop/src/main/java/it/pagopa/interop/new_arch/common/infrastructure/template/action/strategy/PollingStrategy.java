package it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;

@FunctionalInterface
public interface PollingStrategy {

    boolean isSatisfied(ApiResponse response);

    PollingStrategy UNTIL_SUCCESS = PollingStrategy::is2xxSuccessful;

    PollingStrategy UNTIL_ERROR = (code) -> !is2xxSuccessful(code);

    private static boolean is2xxSuccessful(ApiResponse apiResponse) {
        return apiResponse.statusCode() >= 200 && apiResponse.statusCode() < 300;
    }
}