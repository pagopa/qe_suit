package it.pagopa.infrastructure.reporting.contract.resolver;

import it.pagopa.infrastructure.reporting.contract.config.ContractChannelConfig;

public record ContractFactoryContext(
        ContractChannelConfig channel,
        String contractClassName,
        String methodName
) {
}
