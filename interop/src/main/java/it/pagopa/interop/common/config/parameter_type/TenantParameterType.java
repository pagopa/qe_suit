package it.pagopa.interop.common.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.domain.enums.Tenant;

public class TenantParameterType {
    @ParameterType("AgID|Comune di Milano|Comune di Pozzallo|Comune di Comun Nuovo|PagoPA|Kyma|Sogecap|Sogessur")
    public Tenant tenant(String tenant) {
        return Tenant.fromAlias(tenant);
    }
}
