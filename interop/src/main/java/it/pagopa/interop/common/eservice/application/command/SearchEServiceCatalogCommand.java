package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceCatalogSortBy;
import it.pagopa.interop.common.tenant.domain.TenantCategory;

import java.util.List;
import java.util.UUID;

public interface SearchEServiceCatalogCommand {
    SearchEServiceCatalogCommand keyword(String keyword);

    SearchEServiceCatalogCommand onlyActiveEservices(Boolean onlyActiveEservices);

    SearchEServiceCatalogCommand onlyTemplateInstances(Boolean onlyTemplateInstances);

    SearchEServiceCatalogCommand templateId(UUID templateId);

    SearchEServiceCatalogCommand mode(EServiceMode mode);

    SearchEServiceCatalogCommand asyncExchange(Boolean asyncExchange);

    SearchEServiceCatalogCommand onlySignalHubEnabled(Boolean onlySignalHubEnabled);

    SearchEServiceCatalogCommand sortBy(EServiceCatalogSortBy sortBy);

    SearchEServiceCatalogCommand producersIds(List<UUID> producersIds);

    SearchEServiceCatalogCommand producerCategories(List<TenantCategory> categories);

    SearchEServiceCatalogCommand offset(Integer offset);

    SearchEServiceCatalogCommand limit(Integer limit);

    Integer getOffset();

    Integer getLimit();

}
