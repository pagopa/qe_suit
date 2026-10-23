package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.attribute.application.AttributeGateway;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.AttributeOwnershipSelection;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.tenant.application.TenantGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequiredAttributeUseCase {
    private final AttributeGateway attributeGateway;
    private final TenantGateway tenantGateway;

    public Attributes resolve(TenantRef producer, List<RequiredAttributeCommand> requests) {
        if (producer == null || requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Producer and required attribute rows are mandatory");
        }
        List<Attribute> certified = new ArrayList<>();
        List<Attribute> declared = new ArrayList<>();
        List<Attribute> verified = new ArrayList<>();
        var pagoPaAttributes = new EnumMap<AttributeKind, Set<UUID>>(AttributeKind.class);
        var producerAttributes = new EnumMap<AttributeKind, Set<UUID>>(AttributeKind.class);

        for (RequiredAttributeCommand request : requests) {
            List<Attribute> selected = select(producer, request, pagoPaAttributes, producerAttributes);
            switch (request.getKind()) {
                case CERTIFIED, CERTIFIED_DISCRETE -> certified.addAll(selected);
                case DECLARED -> declared.addAll(selected);
                case VERIFIED -> verified.addAll(selected);
            }
        }
        return Attributes.builder()
                .certified(certified)
                .declared(declared)
                .verified(verified)
                .build();
    }

    private List<Attribute> select(
            TenantRef producer,
            RequiredAttributeCommand request,
            EnumMap<AttributeKind, Set<UUID>> pagoPaCache,
            EnumMap<AttributeKind, Set<UUID>> producerCache
    ) {
        Set<UUID> pagoPa = pagoPaCache.computeIfAbsent(request.getKind(), kind -> tenantGateway.getActiveAttributeIds(
                TenantRef.of(Tenant.PAGO_PA.getOrganizationId()), kind));
        Set<UUID> producerAttributes = producerCache.computeIfAbsent(
                request.getKind(), kind -> tenantGateway.getActiveAttributeIds(producer, kind));
        List<UUID> owned = sorted(pagoPa);
        List<UUID> notOwned = producerAttributes.stream()
                .filter(id -> !pagoPa.contains(id))
                .sorted(Comparator.comparing(UUID::toString))
                .toList();

        return switch (request.getSelection()) {
            case OWNED_BY_PAGOPA -> List.of(resolveFirst(owned, request, "posseduto da PagoPA"));
            case NOT_OWNED_BY_PAGOPA -> List.of(resolveFirst(notOwned, request, "non posseduto da PagoPA"));
            case NOT_OWNED_AND_OWNED_BY_PAGOPA -> List.of(
                    resolveFirst(notOwned, request, "non posseduto da PagoPA"),
                    resolveFirst(owned, request, "posseduto da PagoPA"));
        };
    }

    private Attribute resolveFirst(
            List<UUID> candidates, RequiredAttributeCommand request, String ownership
    ) {
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No active " + request.getKind()
                    + " attribute " + ownership + " for group " + request.getGroup());
        }
        Attribute attribute = attributeGateway.getAttribute(new AttributeRef(candidates.get(0)));
        if (attribute.getKind() != request.getKind()) {
            throw new IllegalStateException("Tenant assignment " + attribute.getId()
                    + " has kind " + attribute.getKind() + ", expected " + request.getKind());
        }
        return attribute.toBuilder().group(request.getGroup()).build();
    }

    private List<UUID> sorted(Set<UUID> ids) {
        return ids.stream().sorted(Comparator.comparing(UUID::toString)).toList();
    }
}
