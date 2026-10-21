package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(config = TestMapperConfig.class)
public interface EServiceCatalogSearchConfigurationMapper {
    default void apply(
            List<EServiceCatalogSearchConfiguration> configurations,
            @MappingTarget SearchEServiceCatalogCommand command
    ) {
        Set<String> configured = new HashSet<>();
        for (EServiceCatalogSearchConfiguration configuration : configurations) {
            if (!configured.add(configuration.getFilter())) {
                throw new IllegalArgumentException("Duplicate catalog filter: " + configuration.getFilter());
            }
        }
        configurations.forEach(configuration -> apply(configuration, command));
    }

    @BeanMapping(ignoreByDefault = true)
    void apply(EServiceCatalogSearchConfiguration source, @MappingTarget SearchEServiceCatalogCommand target);

    @AfterMapping
    default void applyFilter(
            EServiceCatalogSearchConfiguration source,
            @MappingTarget SearchEServiceCatalogCommand target
    ) {
        switch (source.getFilter()) {
            case "solo e-service attivi" -> target.onlyActiveEservices(parseBoolean(source.getValue()));
            case "a partire dal risultato" -> target.offset(Integer.valueOf(source.getValue()));
            case "risultati per pagina" -> target.limit(Integer.valueOf(source.getValue()));
            case "modalità" -> target.mode(parseMode(source.getValue()));
            case "scambio asincrono" -> target.asyncExchange(parseBoolean(source.getValue()));
            case "Signal Hub" -> target.onlySignalHubEnabled(parseSignalHub(source.getValue()));
            case "erogatore" -> target.producersIds(parseProducers(source.getValue()));
            default -> throw new IllegalArgumentException("Unsupported catalog filter: " + source.getFilter());
        }
    }

    private static List<UUID> parseProducers(String value) {
        if ("elenco vuoto".equals(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(" e ", -1))
                .map(String::trim)
                .map(Tenant::fromAlias)
                .map(Tenant::getOrganizationId)
                .toList();
    }

    private static EServiceMode parseMode(String value) {
        return switch (value) {
            case "erogazione" -> EServiceMode.DELIVER;
            case "ricezione" -> EServiceMode.RECEIVE;
            default -> throw new IllegalArgumentException("Modalità e-service non supportata: " + value);
        };
    }

    private static boolean parseSignalHub(String value) {
        return switch (value) {
            case "attivo" -> true;
            case "non attivo" -> false;
            default -> throw new IllegalArgumentException("Valore Signal Hub non supportato: " + value);
        };
    }

    private static boolean parseBoolean(String value) {
        return switch (value) {
            case "sì", "si" -> true;
            case "no" -> false;
            default -> throw new IllegalArgumentException("Invalid catalog boolean: " + value);
        };
    }
}
