package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.interop.web.eservice_template.infrastructure.page.component.EServiceTemplateGeneralInfoSection;
import it.pagopa.interop.web.eservice_template.infrastructure.page.component.EServiceTemplateTechnicalInfoSection;
import it.pagopa.interop.web.eservice_template.infrastructure.page.component.EServiceTemplateThresholdsAndAttributesSection;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.consumer-e-service-template-catalog}/${eServiceTemplateId}/${eServiceTemplateVersionId}")
public interface ConsumerEServiceTemplateDetailsPage extends Page {

    @XPath(".//a[normalize-space()='Torna al catalogo'] | .//button[normalize-space()='Torna al catalogo']")
    Button backToCatalogButton();

    @XPath(".//h1")
    Readable<String> pageTitle();

    @XPath(".//div[contains(@class,'MuiChip-root')]")
    Chip statusChip();

    @XPath(".//button[normalize-space()='Usa template']")
    Button useTemplateButton();

    @XPath(".//button[@role='tab' and contains(.,'Dettagli template')]")
    Button detailsTabButton();

    @XPath(".//button[@role='tab' and contains(.,'Istanze generate')]")
    Button instancesTabButton();

    @XPath(".//section[.//h2[normalize-space()='Informazioni generali']]")
    EServiceTemplateGeneralInfoSection generalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Specifiche tecniche e-service']]")
    EServiceTemplateTechnicalInfoSection technicalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Soglie e attributi']]")
    EServiceTemplateThresholdsAndAttributesSection thresholdsAndAttributesSection();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> pageTitle().read(),
                name -> !name.isBlank(),
                "Consumer E-Service Template Detail Page is not loaded yet. Page title is blank"
        );
    }
}

