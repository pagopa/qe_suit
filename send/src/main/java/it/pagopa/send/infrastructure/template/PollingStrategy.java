package it.pagopa.send.infrastructure.template;

@FunctionalInterface
public interface PollingStrategy {

    boolean isSatisfied(ApiResponse response);

    PollingStrategy UNTIL_SUCCESS = response -> response != null && response.is2xxSuccessful();
}
