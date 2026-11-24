package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.base-url}/erogazione/template-eservice")
public interface ProducerEServiceTemplatesListPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1[normalize-space()='I miei template e-service']")
    Label title();

    @XPath(".//p[contains(@class, 'MuiTypography-root') and contains(normalize-space(), 'Gestisci ed eroga nuovi template di e-service')]")
    Label subtitle();

    @XPath(".//button[normalize-space()='Crea nuovo']")
    Button createNewButton();

    @XPath(".//label[@for = //input[@name='q']/@id]")    Label searchLabel();

    @XPath(".//input[@name='q']")
    TextField searchInput();

    @XPath(".//input[@name='q']/ancestor::div[contains(@class, 'MuiOutlinedInput-root')][1]//button")
    Button searchButton();

    @XPath(".//table")
    Table templatesTable();

    Pagination pagination();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                text -> text != null && !text.isBlank(),
                "Producer EService Template List Page is not loaded or its title is absent"
        );
    }
}