package it.pagopa.interop.common.infrastructure.reporting.contract.resolver;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractChannelConfig;
import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractReportConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContractChannelResolver {

    private final ContractReportConfig config;

    public ContractChannelResolver(ContractReportConfig config) {
        this.config = config;
    }

    public Optional<ResolvedContractChannel> resolveByClassName(String simpleClassName) {
        if (simpleClassName == null || simpleClassName.isBlank()) {
            return Optional.empty();
        }
        List<ResolvedContractChannel> matches = new ArrayList<>();
        for (Map.Entry<String, ContractChannelConfig> entry : config.channels().entrySet()) {
            ContractChannelConfig channel = entry.getValue();
            if (simpleClassName.startsWith(channel.classPrefix())) {
                matches.add(new ResolvedContractChannel(entry.getKey(), channel));
            }
        }
        if (matches.size() > 1) {
            throw new IllegalStateException("Ambiguous channel for class '" + simpleClassName + "': " + matches.stream().map(ResolvedContractChannel::key).toList());
        }
        return matches.stream().findFirst();
    }
}
