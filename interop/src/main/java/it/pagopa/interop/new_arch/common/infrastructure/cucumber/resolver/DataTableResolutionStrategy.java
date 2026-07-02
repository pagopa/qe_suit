package it.pagopa.interop.new_arch.common.infrastructure.cucumber.resolver;

public interface DataTableResolutionStrategy {
    String getFunctionName();
    String resolve(String argument);
}
