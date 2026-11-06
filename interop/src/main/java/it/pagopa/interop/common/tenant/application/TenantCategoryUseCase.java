package it.pagopa.interop.common.tenant.application;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.tenant.application.command.CheckTenantCategoryCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class TenantCategoryUseCase {
    private final AttributeGateway attributeGateway;
    private final TenantGateway tenantGateway;

    public void check(CheckTenantCategoryCommand command) {
        Set<UUID> categoryAttributes = new HashSet<>();
        for (String code : command.getCategory().getIpaCodes()) {
            attributeGateway.findAttributeByOriginAndCode("IPA", code)
                    .ifPresent(attribute -> categoryAttributes.add(attribute.getId()));
        }
        assertThat(categoryAttributes)
                .as("Category precondition for %s: no IPA attributes found for codes %s",
                        command.getCategory(), command.getCategory().getIpaCodes())
                .isNotEmpty();
        Set<UUID> activeAttributes = tenantGateway.getActiveCertifiedAttributeIds(command.getTenant());
        boolean member = activeAttributes.stream().anyMatch(categoryAttributes::contains);
        assertThat(member)
                .as("Category precondition for tenant %s, category %s, active certified attributes %s",
                        command.getTenant().id(), command.getCategory(), activeAttributes)
                .isEqualTo(command.isExpectedMembership());
    }
}
