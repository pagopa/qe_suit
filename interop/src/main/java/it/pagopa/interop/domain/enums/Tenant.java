package it.pagopa.interop.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static it.pagopa.interop.domain.enums.TenantType.*;

@RequiredArgsConstructor
@Getter
public enum Tenant {
    AGID(PA, "AgID", "AgID",  UUID.fromString("0e9e2dab-2e93-4f24-ba59-38d9f11198ca"), UUID.fromString("83e6fc9d-7f60-4a62-8cec-785524b6a0a5")),
    COMUNE_DI_MILANO(PA, "Comune di Milano", "Comune di Milano", UUID.fromString("e79a24cd-8edc-441e-ae8d-e87c3aea0059"), UUID.fromString("026e8c72-7944-4dcd-8668-f596447fec6d")),
    COMUNE_DI_POZZALLO(PA, "Comune di Pozzallo","Comune di Pozzallo", UUID.fromString("80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20"), UUID.fromString("0294041f-e0f8-451e-a21e-bc4aa4f6a8ca")),
    COMUNE_DI_COMUN_NUOVO(PA, "Comune di Comun Nuovo", "Comune di Comun Nuovo", UUID.fromString("951cc51a-1dba-42e0-a7d8-41f80bd045b9"), UUID.fromString("88547b14-b004-473c-a4ee-5a732cd92dbb")),
    PAGO_PA(GSP, "PagoPA", "PagoPA",  UUID.fromString("69e2865e-65ab-4e48-a638-2037a9ee2ee7"), UUID.fromString("962d21c-c701-4805-93f6-53a877898756")),
    KYMA(GSP, "KYMA MOBILITA' S.P.A.", "Kyma", UUID.fromString("f6e264d2-e1bc-4e8a-be54-5c05b4ea6e45"), UUID.fromString("037be1b6-6229-4abf-ab68-f433a5dce0f9")),
    SOGECAP(PRIVATE, "SOGECAP", "Sogecap", UUID.fromString("a7a87b2c-5742-43fb-a847-6304bb0414b4"), UUID.fromString("335a4117-5044-4d28-9473-70b74d3bf072")),
    SOGESSUR(PRIVATE, "SOGESSUR SOCIETE' ANONYME", "Sogessur", UUID.fromString("4c193f7f-40ac-4754-9bb3-7c99c4f38a4a"), UUID.fromString("5a4e32bf-3c3d-4a5f-b5ba-1ea105e95f51"));

    private final TenantType tenantType;
    private final String name;
    private final String alias;
    private final UUID organizationId;
    private final UUID selfcareId;

    public static Tenant fromOrganizationId(UUID organizationId) {
        for (Tenant tenant : Tenant.values()) {
            if (tenant.getOrganizationId().equals(organizationId)) {
                return tenant;
            }
        }
        throw new IllegalArgumentException("No enum constant for organizationId " + organizationId);
    }

    public static Tenant fromSelfcareId(UUID selfcareId) {
        for (Tenant tenant : Tenant.values()) {
            if (tenant.getSelfcareId().equals(selfcareId)) {
                return tenant;
            }
        }
        throw new IllegalArgumentException("No enum constant for selfcareId " + selfcareId);
    }

    public static Tenant fromName(String name) {
        for (Tenant tenant : Tenant.values()) {
            if (tenant.getName().equalsIgnoreCase(name)) {
                return tenant;
            }
        }
        throw new IllegalArgumentException("No enum constant for name " + name);
    }

    public static Tenant fromTenantType(TenantType tenantType) {
    for (Tenant tenant : Tenant.values()) {
        if (tenant.getTenantType() == tenantType) {
            return tenant;
        }
    }
    throw new IllegalArgumentException("No enum constant for tenantType " + tenantType);
}

    public static Tenant fromAlias(String alias) {
        for (Tenant tenant : Tenant.values()) {
            if (tenant.getAlias().equalsIgnoreCase(alias)) {
                return tenant;
            }
        }
        throw new IllegalArgumentException("No enum constant for alias " + alias);
    }
}
