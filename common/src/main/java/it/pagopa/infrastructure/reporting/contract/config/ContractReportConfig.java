package it.pagopa.infrastructure.reporting.contract.config;

import java.util.Map;

public record ContractReportConfig(Map<String, ContractChannelConfig> channels) {
}
