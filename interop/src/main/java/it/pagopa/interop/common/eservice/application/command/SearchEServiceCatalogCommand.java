package it.pagopa.interop.common.eservice.application.command;

public interface SearchEServiceCatalogCommand {
    SearchEServiceCatalogCommand keyword(String keyword);

    SearchEServiceCatalogCommand onlyActiveEservices(Boolean onlyActiveEservices);

    SearchEServiceCatalogCommand offset(Integer offset);

    SearchEServiceCatalogCommand limit(Integer limit);
}
