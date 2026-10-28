package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

import it.pagopa.interop.common.eservice.domain.EService;
import lombok.Value;

@Value
public class SelectedEServiceDescriptor {
    EService eService;
    int version;
}
