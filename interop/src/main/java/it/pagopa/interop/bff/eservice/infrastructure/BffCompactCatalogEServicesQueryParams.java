package it.pagopa.interop.bff.eservice.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.PersonalDataFilter;
import lombok.Data;

import java.util.List;

@Data
public class BffCompactCatalogEServicesQueryParams {

    private String q;
    private List<EServiceDescriptorState> states;
    private List<AgreementState> agreementStates;
    private EServiceMode mode;

    @JsonProperty("isConsumerDelegable")
    private Boolean isConsumerDelegable;

    private PersonalDataFilter personalData;
    private Integer offset;
    private Integer limit;

    @JsonProperty("isConsumerDelegable")
    public Boolean getIsConsumerDelegable() {
        return isConsumerDelegable;
    }

    @JsonProperty("isConsumerDelegable")
    public void setIsConsumerDelegable(Boolean isConsumerDelegable) {
        this.isConsumerDelegable = isConsumerDelegable;
    }
}
