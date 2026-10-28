package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.RequiredAttributeConfiguration;
import it.pagopa.interop.common.kernel.domain.AttributeRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequiredAttributeConfigurationMapper {
    private final EntityStore entityStore;

    public List<RequiredAttributeCommand> toRequests(List<RequiredAttributeConfiguration> configurations) {
        if (configurations == null || configurations.isEmpty()) {
            throw new IllegalArgumentException("At least one required attribute row is required");
        }
        List<RequiredAttributeCommand> requests = configurations.stream()
                .map(this::toRequest)
                .toList();
        validateGroups(requests);
        validateDistinctReferences(requests);
        return requests;
    }

    private RequiredAttributeCommand toRequest(RequiredAttributeConfiguration configuration) {
        if (configuration == null || configuration.getKind() == null || configuration.getGroup() == null
                || configuration.getAttribute() == null) {
            throw new IllegalArgumentException("Required attribute rows need tipo, gruppo and attributo");
        }
        AttributeKind kind = switch (configuration.getKind().strip().toLowerCase(Locale.ROOT)) {
            case "certificato" -> AttributeKind.CERTIFIED;
            case "dichiarato" -> AttributeKind.DECLARED;
            case "verificato" -> AttributeKind.VERIFIED;
            case "discreto" -> AttributeKind.CERTIFIED_DISCRETE;
            default -> throw new IllegalArgumentException("Unsupported required attribute type: "
                    + configuration.getKind());
        };
        String value = configuration.getAttribute().strip();
        if (!value.matches("(CERT|DECL|VER|DISC)-[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Required attribute must be a registered alias: " + value);
        }
        Attribute attribute = entityStore.getByAliasOrThrow(value, Attribute.class);
        if (attribute.getKind() != kind) {
            throw new IllegalArgumentException("Attribute alias " + value + " has kind " + attribute.getKind()
                    + ", expected " + kind);
        }
        return new RequiredAttributeCommand(kind, configuration.getGroup(), attribute.getRef());
    }

    private void validateDistinctReferences(List<RequiredAttributeCommand> requests) {
        Set<UUID> references = new HashSet<>();
        requests.stream().map(RequiredAttributeCommand::getAttributeRef)
                .map(AttributeRef::id)
                .forEach(id -> {
                    if (!references.add(id)) {
                        throw new IllegalArgumentException(
                                "An aliased attribute can be required only once across groups: " + id);
                    }
                });
    }

    private void validateGroups(List<RequiredAttributeCommand> requests) {
        Map<AttributeKind, Set<Integer>> groups = new EnumMap<>(AttributeKind.class);
        requests.forEach(request -> groups.computeIfAbsent(
                request.getKind() == AttributeKind.CERTIFIED_DISCRETE
                        ? AttributeKind.CERTIFIED : request.getKind(), ignored -> new HashSet<>())
                .add(request.getGroup()));
        groups.forEach((kind, values) -> {
            int largest = values.stream().mapToInt(Integer::intValue).max().orElse(0);
            if (values.size() != largest) {
                throw new IllegalArgumentException("Groups for " + kind + " must be contiguous starting at 1");
            }
        });
    }
}
