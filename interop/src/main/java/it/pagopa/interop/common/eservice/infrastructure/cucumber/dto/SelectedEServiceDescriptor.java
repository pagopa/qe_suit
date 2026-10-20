package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import lombok.Value;

@Value
public class SelectedEServiceDescriptor {
    EService eService;
    EServiceDescriptor descriptor;
}
