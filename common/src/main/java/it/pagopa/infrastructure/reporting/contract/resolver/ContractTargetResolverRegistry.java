package it.pagopa.infrastructure.reporting.contract.resolver;

import it.pagopa.infrastructure.reporting.contract.config.ContractTargetType;

import java.util.EnumMap;
import java.util.Map;

public class ContractTargetResolverRegistry {

    private final Map<ContractTargetType, ContractTargetResolver> resolvers;

    public ContractTargetResolverRegistry(Map<ContractTargetType, ContractTargetResolver> resolvers) {
        this.resolvers = new EnumMap<>(ContractTargetType.class);
        this.resolvers.putAll(resolvers);
    }

    public ContractTargetResolver get(ContractTargetType type) {
        ContractTargetResolver resolver = resolvers.get(type);
        if (resolver == null) {
            throw new IllegalStateException("No ContractTargetResolver registered for target-type " + type);
        }
        return resolver;
    }
}
