package it.pagopa.interop.web.purpose_template.infrastructure.suite.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.interop.web.purpose_template.infrastructure.suite.component.PurposeTemplateCatalogFilter;

@Url("${interop.web.base-url}/fruizione/catalogo-template-finalita")
public interface ConsumerPurposeTemplateCatalogPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> title();

    @XPath(".//h1/following-sibling::p[1]")
    Readable<String> description();

    @XPath(".//input[@name='q']/ancestor::div[contains(@class, 'MuiFormControl-root')][1]")
    PurposeTemplateCatalogFilter nameFilter();

    @XPath("(.//input[@role='combobox'])[1]/ancestor::div[contains(@class, 'MuiFormControl-root')][1]")
    PurposeTemplateCatalogFilter creatorFilter();

    @XPath("(.//input[@role='combobox'])[2]/ancestor::div[contains(@class, 'MuiFormControl-root')][1]")
    PurposeTemplateCatalogFilter eServiceFilter();

    @XPath("(.//input[@role='combobox'])[3]/ancestor::div[contains(@class, 'MuiFormControl-root')][1]")
    PurposeTemplateCatalogFilter targetTenantKindFilter();

    default ConsumerPurposeTemplateCatalogPage setNameFilter(String name) {
        nameFilter().input().fill(name);
        return this;
    }

    default ConsumerPurposeTemplateCatalogPage setCreatorFilter(String creator) {
        creatorFilter().input().fill(creator);
        return this;
    }

    default ConsumerPurposeTemplateCatalogPage setEServiceFilter(String eService) {
        eServiceFilter().input().fill(eService);
        return this;
    }

    default ConsumerPurposeTemplateCatalogPage setTargetTenantKindFilter(String targetTenantKind) {
        targetTenantKindFilter().input().fill(targetTenantKind);
        return this;
    }

    default String getTitle() {
        return title().read();
    }

    default String getDescription() {
        return description().read();
    }

    @Override
    default void assertLoaded() {
        title().readAndAssert("Compilazione agevolata delle finalità");
    }
}
