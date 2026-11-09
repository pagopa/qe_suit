package it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper;

import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceCatalogSortBy;
import it.pagopa.interop.common.eservice.domain.EServiceCatalogRequesterDelegationRole;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.infrastructure.config.TestMapperConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.tenant.domain.TenantCategory;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Context;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;

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
        apply(configurations, command, null);
    }

    default void apply(
            List<EServiceCatalogSearchConfiguration> configurations,
            @MappingTarget SearchEServiceCatalogCommand command,
            @Context EntityStore entities
    ) {
        Set<String> configured = new HashSet<>();
        for (EServiceCatalogSearchConfiguration configuration : configurations) {
            if (!configured.add(configuration.getFilter())) {
                throw new IllegalArgumentException("Duplicate catalog filter: " + configuration.getFilter());
            }
        }
        configurations.forEach(configuration -> apply(configuration, command, entities));
    }

    @BeanMapping(ignoreByDefault = true)
    void apply(EServiceCatalogSearchConfiguration source, @MappingTarget SearchEServiceCatalogCommand target,
               @Context EntityStore entities);

    @AfterMapping
    default void applyFilter(
            EServiceCatalogSearchConfiguration source,
            @MappingTarget SearchEServiceCatalogCommand target,
            @Context EntityStore entities
    ) {
        switch (source.getFilter()) {
            case "solo e-service attivi" -> target.onlyActiveEservices(parseBoolean(source.getValue()));
            case "richiedibili dal fruitore" -> target.availableForRequester(parseBoolean(source.getValue()));
            case "sottoscritti dal fruitore" -> target.subscribedByRequester(parseBoolean(source.getValue()));
            case "ruolo di delega" -> target.requesterDelegationRoles(parseDelegationRoles(source.getValue()));
            case "con template di finalità collegati" ->
                    target.hasLinkedPurposeTemplates(parseBoolean(source.getValue()));
            case "istanze di template" -> target.onlyTemplateInstances(parseBoolean(source.getValue()));
            case "istanza del template" -> {
                if (entities == null) throw new IllegalArgumentException("Template alias resolution requires EntityStore");
                target.templateId(entities.getByAliasOrThrow(source.getValue(), EServiceTemplate.class).getId());
            }
            case "a partire dal risultato" -> target.offset(Integer.valueOf(source.getValue()));
            case "risultati per pagina" -> target.limit(Integer.valueOf(source.getValue()));
            case "modalità" -> target.mode(parseMode(source.getValue()));
            case "scambio asincrono" -> target.asyncExchange(parseBoolean(source.getValue()));
            case "Signal Hub" -> target.onlySignalHubEnabled(parseSignalHub(source.getValue()));
            case "ordinamento" -> target.sortBy(parseSortBy(source.getValue()));
            case "erogatore" -> target.producersIds(parseProducers(source.getValue()));
            case "categoria dell'erogatore" -> target.producerCategories(parseCategories(source.getValue()));
            default -> throw new IllegalArgumentException("Unsupported catalog filter: " + source.getFilter());
        }
    }

    private static List<TenantCategory> parseCategories(String value) {
        if ("elenco vuoto".equals(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(" e ", -1))
                .map(String::trim)
                .map(TenantCategory::valueOf)
                .toList();
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

    private static EServiceCatalogSortBy parseSortBy(String value) {
        return switch (value) {
            case "nome crescente" -> EServiceCatalogSortBy.NAME_ASC;
            case "nome decrescente" -> EServiceCatalogSortBy.NAME_DESC;
            case "data di creazione crescente" -> EServiceCatalogSortBy.CREATED_AT_ASC;
            case "data di creazione decrescente" -> EServiceCatalogSortBy.CREATED_AT_DESC;
            default -> throw new IllegalArgumentException("Ordinamento catalogo non supportato: " + value);
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

    private static List<EServiceCatalogRequesterDelegationRole> parseDelegationRoles(String value) {
        return switch (value) {
            case "delegato" -> List.of(EServiceCatalogRequesterDelegationRole.DELEGATE);
            case "delegante" -> List.of(EServiceCatalogRequesterDelegationRole.DELEGATOR);
            case "delegato e delegante" -> List.of(
                    EServiceCatalogRequesterDelegationRole.DELEGATE,
                    EServiceCatalogRequesterDelegationRole.DELEGATOR
            );
            default -> throw new IllegalArgumentException("Ruolo di delega non supportato: " + value);
        };
    }
}
