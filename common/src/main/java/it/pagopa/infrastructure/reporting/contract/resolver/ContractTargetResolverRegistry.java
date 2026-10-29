package it.pagopa.infrastructure.reporting.contract.resolver;

import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;

import it.pagopa.infrastructure.reporting.contract.openapi.OpenApiContractTargetResolver;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class ContractTargetResolverRegistry {

    private final Map<ContractTargetType, ContractTargetResolver> resolvers;

    public ContractTargetResolverRegistry(Map<ContractTargetType, ContractTargetResolver> resolvers) {
        this.resolvers = new EnumMap<>(ContractTargetType.class);
        this.resolvers.putAll(resolvers);
    }

    public static ContractTargetResolverRegistry defaults() {
        return defaults(Path.of("src/test/java"));
    }

    public static ContractTargetResolverRegistry defaults(Path sourceRoot) {
        return new ContractTargetResolverRegistry(Map.of(
                ContractTargetType.OPENAPI, new OpenApiContractTargetResolver(),
                ContractTargetType.PAGE, new PageContractTargetResolver(sourceRoot)
        ));
    }

    public ContractTargetResolver get(ContractTargetType type) {
        ContractTargetResolver resolver = resolvers.get(type);
        if (resolver == null) {
            throw new IllegalStateException("No ContractTargetResolver registered for target-type " + type);
        }
        return resolver;
    }
}

