package it.pagopa.send.infrastructure.template;

@FunctionalInterface
public interface PollingStrategy {

    PollingStrategy UNTIL_SUCCESS = response -> response != null && response.is2xxSuccessful();

    boolean isSatisfied(ApiResponse response);
}
