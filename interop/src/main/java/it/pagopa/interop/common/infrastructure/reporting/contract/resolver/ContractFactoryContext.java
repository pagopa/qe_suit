package it.pagopa.interop.common.infrastructure.reporting.contract.resolver;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractChannelConfig;

public record ContractFactoryContext(
        ContractChannelConfig channel,
        String contractClassName,
        String methodName
) {
}
