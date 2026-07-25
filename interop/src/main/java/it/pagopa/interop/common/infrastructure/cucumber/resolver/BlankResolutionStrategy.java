package it.pagopa.interop.new_arch.common.infrastructure.cucumber.resolver;

import org.springframework.stereotype.Component;

@Component
class BlankResolutionStrategy implements DataTableResolutionStrategy {
    @Override
    public String getFunctionName() {
        return "blank";
    }

    @Override
    public String resolve(String argument) {
        return "";
    }
}
