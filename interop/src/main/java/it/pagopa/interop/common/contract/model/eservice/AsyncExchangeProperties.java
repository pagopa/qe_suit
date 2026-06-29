package it.pagopa.interop.common.contract.model.eservice;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class AsyncExchangeProperties {
    Integer responseTime;
    Integer resourceAvailableTime;
    Boolean confirmation;
    Boolean bulk;
    Integer maxResultSet;
}


