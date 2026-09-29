package it.pagopa.infrastructure.reporting.contract.resolver;

import it.pagopa.infrastructure.reporting.contract.config.ContractChannelConfig;

public record ResolvedContractChannel(
        String key,
        ContractChannelConfig config
) {
}
