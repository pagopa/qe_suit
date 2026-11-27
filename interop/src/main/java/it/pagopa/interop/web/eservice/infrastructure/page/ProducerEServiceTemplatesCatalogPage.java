package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.web.eservice.infrastructure.page.component.EServiceTemplateCatalogCard;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

import java.util.List;
import java.util.Optional;

@Url("${interop.web.base-url}/erogazione/catalogo-template")
public interface ProducerEServiceTemplatesCatalogPage extends Page {

    String EXPECTED_TITLE = "Catalogo dei template di e-service";

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//h1/following-sibling::p[1]")
    Readable<String> pageDescription();

    @XPath(".//label[@for = ancestor::main//input[@name='q']/@id]")
    Readable<String> nameFilterLabel();

    @XPath(".//input[@name='q']")
    TextField nameFilterInput();

    @XPath(".//label[@for = ancestor::main//input[@role='combobox']/@id]")
    Readable<String> creatorFilterLabel();

    @XPath(".//input[@role='combobox']")
    TextField creatorFilterInput();

    @XPath(".//div[normalize-space(.)='Cerca per nome']/button")
    Button searchButton();

    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert emptyStateAlert();

    List<EServiceTemplateCatalogCard> templateCards();

    Pagination pagination();

    default Optional<EServiceTemplateCatalogCard> findCardByTitle(String title) {
        return templateCards().stream()
                .filter(card -> title.equals(card.title().read()))
                .findFirst();
    }

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                EXPECTED_TITLE::equals,
                "Producer e-service templates catalog page is not loaded yet"
        );
    }
}

