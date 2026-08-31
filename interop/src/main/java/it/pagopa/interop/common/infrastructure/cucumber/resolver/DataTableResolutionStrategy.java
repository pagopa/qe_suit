package it.pagopa.interop.common.infrastructure.cucumber.resolver;

public interface DataTableResolutionStrategy {
    String getFunctionName();
    String resolve(String argument);
}
