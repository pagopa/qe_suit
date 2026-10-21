package it.pagopa.interop.common.eservice.application.command;

import it.pagopa.interop.common.eservice.domain.EServiceMode;

public interface SearchEServiceCatalogCommand {
    SearchEServiceCatalogCommand keyword(String keyword);

    SearchEServiceCatalogCommand onlyActiveEservices(Boolean onlyActiveEservices);

    SearchEServiceCatalogCommand mode(EServiceMode mode);

    SearchEServiceCatalogCommand asyncExchange(Boolean asyncExchange);

    SearchEServiceCatalogCommand onlySignalHubEnabled(Boolean onlySignalHubEnabled);

    SearchEServiceCatalogCommand offset(Integer offset);

    SearchEServiceCatalogCommand limit(Integer limit);
}
