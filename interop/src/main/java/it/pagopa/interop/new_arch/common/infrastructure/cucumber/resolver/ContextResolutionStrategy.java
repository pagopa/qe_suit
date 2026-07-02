package it.pagopa.interop.new_arch.common.infrastructure.cucumber.resolver;

import it.pagopa.interop.new_arch.common.client.domain.Client;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.ScenarioContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Component
class ContextResolutionStrategy implements DataTableResolutionStrategy {
    private final Map<String, Supplier<String>> contextTokens;

    public ContextResolutionStrategy(ScenarioContext scenarioContext) {
        this.contextTokens = Map.of(
                "clientId", () -> scenarioContext.getLastOrThrow(Client.class).getId().toString()
        );
    }

    @Override
    public String getFunctionName() {
        return "retrieve";
    }

    @Override
    public String resolve(String argument) {
        Supplier<String> supplier = contextTokens.get(argument);

        if (supplier == null) {
            throw new IllegalArgumentException("Proprietà di scommessa non supportata in $retrieve: " + argument);
        }

        return supplier.get();
    }
}
