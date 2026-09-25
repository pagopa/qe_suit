package it.pagopa.interop.common.infrastructure.reporting.contract.openapi;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractTargetType;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.ContractFactoryContext;
import it.pagopa.interop.common.infrastructure.reporting.contract.resolver.ContractTargetResolver;

import java.util.HashMap;
import java.util.Map;

public class OpenApiContractTargetResolver implements ContractTargetResolver {

    private final Map<String, OpenApiOperationIndex> indexByLocation = new HashMap<>();

    @Override
    public String resolveTarget(ContractFactoryContext context) {
        if (context.channel().targetType() != ContractTargetType.OPENAPI) {
            throw new IllegalStateException("OpenApiContractTargetResolver cannot resolve target-type " + context.channel().targetType());
        }
        String location = context.channel().openapi();
        if (location == null || location.isBlank()) {
            throw new IllegalStateException("Missing OpenAPI location for channel " + context.channel().key());
        }
        String operationId = context.methodName();
        if (operationId == null || operationId.isBlank()) {
            throw new IllegalStateException("Missing @TestFactory method name for OPENAPI target resolution");
        }
        OpenApiOperationIndex index = indexByLocation.computeIfAbsent(location, OpenApiOperationIndex::new);
        return index.resolve(operationId);
    }
}
