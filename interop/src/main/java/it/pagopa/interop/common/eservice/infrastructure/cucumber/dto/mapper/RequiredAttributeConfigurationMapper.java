package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.AttributeOwnershipSelection;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.RequiredAttributeConfiguration;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RequiredAttributeConfigurationMapper {

    public List<RequiredAttributeCommand> toRequests(List<RequiredAttributeConfiguration> configurations) {
        if (configurations == null || configurations.isEmpty()) {
            throw new IllegalArgumentException("At least one required attribute row is required");
        }
        List<RequiredAttributeCommand> requests = configurations.stream()
                .map(this::toRequest)
                .toList();
        validateGroups(requests);
        return requests;
    }

    private RequiredAttributeCommand toRequest(RequiredAttributeConfiguration configuration) {
        if (configuration == null || configuration.getKind() == null || configuration.getGroup() == null
                || configuration.getAttribute() == null) {
            throw new IllegalArgumentException("Required attribute rows need tipo, gruppo and attributo");
        }
        AttributeKind kind = switch (configuration.getKind().strip().toLowerCase(java.util.Locale.ROOT)) {
            case "certificato" -> AttributeKind.CERTIFIED;
            case "dichiarato" -> AttributeKind.DECLARED;
            case "verificato" -> AttributeKind.VERIFIED;
            case "discreto" -> AttributeKind.CERTIFIED_DISCRETE;
            default -> throw new IllegalArgumentException("Unsupported required attribute type: "
                    + configuration.getKind());
        };
        AttributeOwnershipSelection selection = switch (configuration.getAttribute().strip()) {
            case "un attributo posseduto da PagoPA" -> AttributeOwnershipSelection.OWNED_BY_PAGOPA;
            case "un attributo non posseduto da PagoPA" -> AttributeOwnershipSelection.NOT_OWNED_BY_PAGOPA;
            case "un attributo non posseduto e uno posseduto da PagoPA" ->
                    AttributeOwnershipSelection.NOT_OWNED_AND_OWNED_BY_PAGOPA;
            default -> throw new IllegalArgumentException("Unsupported attribute selector: "
                    + configuration.getAttribute());
        };
        return new RequiredAttributeCommand(kind, configuration.getGroup(), selection);
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
