package it.pagopa.interop.common.infrastructure.cucumber.resolver;

import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.context.EntityStore;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Component
class ContextResolutionStrategy implements DataTableResolutionStrategy {
    private final Map<String, Supplier<String>> contextTokens;

    public ContextResolutionStrategy(EntityStore entityStore) {
        this.contextTokens = Map.of(
                "clientId", () -> entityStore.getLastOrThrow(Client.class).getId().toString()
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
