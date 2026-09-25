package it.pagopa.interop.common.infrastructure.reporting.contract.config;

import java.util.HashMap;
import java.util.Map;

public class ContractReportConfigurationValidator {

    public void validate(ContractReportConfig config) {
        if (config == null || config.channels() == null || config.channels().isEmpty()) {
            throw new IllegalStateException("contract-report.channels is missing or empty");
        }
        Map<String, String> prefixes = new HashMap<>();
        for (Map.Entry<String, ContractChannelConfig> entry : config.channels().entrySet()) {
            String channelKey = entry.getKey();
            ContractChannelConfig channel = entry.getValue();
            requireNonBlank(channel.label(), "contract-report.channels." + channelKey + ".label is required");
            requireNonBlank(channel.classPrefix(), "contract-report.channels." + channelKey + ".class-prefix is required");
            if (channel.targetType() == null) {
                throw new IllegalStateException("contract-report.channels." + channelKey + ".target-type is required");
            }
            String prefixKey = channel.classPrefix().trim().toLowerCase();
            String previous = prefixes.putIfAbsent(prefixKey, channelKey);
            if (previous != null) {
                throw new IllegalStateException("Duplicate class-prefix detected between channels '" + previous + "' and '" + channelKey + "'");
            }
            if (channel.targetType() == ContractTargetType.OPENAPI && isBlank(channel.openapi())) {
                throw new IllegalStateException("contract-report.channels." + channelKey + ".openapi is required for target-type OPENAPI");
            }
        }
    }

    private void requireNonBlank(String value, String message) {
        if (isBlank(value)) {
            throw new IllegalStateException(message);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
