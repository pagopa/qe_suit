package it.pagopa.interop.common.infrastructure.template.action.strategy;

import it.pagopa.interop.common.infrastructure.response.RawResponse;

@FunctionalInterface
public interface PollingStrategy {

    boolean isSatisfied(RawResponse response);

    PollingStrategy UNTIL_SUCCESS = RawResponse::isSuccess;

    PollingStrategy UNTIL_ERROR = (response) -> !response.isSuccess();

}