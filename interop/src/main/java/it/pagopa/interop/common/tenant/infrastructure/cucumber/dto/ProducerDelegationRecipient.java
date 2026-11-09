package it.pagopa.interop.common.tenant.infrastructure.cucumber.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import it.pagopa.interop.common.kernel.domain.Tenant;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProducerDelegationRecipient {
    @JsonAlias("ente")
    @NotNull
    private Tenant tenant;
}
