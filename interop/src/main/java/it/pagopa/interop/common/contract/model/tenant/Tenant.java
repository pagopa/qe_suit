package it.pagopa.interop.common.contract.model.tenant;

import it.pagopa.interop.common.contract.enums.TenantType;
import it.pagopa.interop.common.contract.model.Attribute;
import it.pagopa.interop.common.contract.model.TestModel;
import lombok.*;
import lombok.experimental.Delegate;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant implements TestModel {
    private UUID id;
    private UUID selfcareId;
    private TenantType kind;
    private String name;
    private List<Attribute> declaredAttributes;
    private List<Attribute> certifiedAttributes;
    private List<Attribute> verifiedAttributes;
}
