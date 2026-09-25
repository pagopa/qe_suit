package it.pagopa.interop.common.infrastructure.reporting.contract.config;

public record ContractChannelConfig(
        String key,
        String label,
        String classPrefix,
        ContractTargetType targetType,
        String openapi
) {
}
