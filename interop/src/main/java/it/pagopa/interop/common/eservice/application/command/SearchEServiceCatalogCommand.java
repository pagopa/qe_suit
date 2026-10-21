package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.eservice.domain.EServiceMode;

import java.util.List;
import java.util.UUID;

public interface SearchEServiceCatalogCommand {
    SearchEServiceCatalogCommand keyword(String keyword);

    SearchEServiceCatalogCommand onlyActiveEservices(Boolean onlyActiveEservices);

    SearchEServiceCatalogCommand mode(EServiceMode mode);

    SearchEServiceCatalogCommand asyncExchange(Boolean asyncExchange);

    SearchEServiceCatalogCommand onlySignalHubEnabled(Boolean onlySignalHubEnabled);

    SearchEServiceCatalogCommand producersIds(List<UUID> producersIds);

    SearchEServiceCatalogCommand offset(Integer offset);

    SearchEServiceCatalogCommand limit(Integer limit);
}
