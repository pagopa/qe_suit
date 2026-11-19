package it.pagopa.interop.web.eservice_template.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;

public interface ProducerEServiceTemplateDetailsTab extends Component {

    @XPath(".//section[.//h2[normalize-space()='Informazioni template']]")
    EServiceTemplateGeneralInfoSection generalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Specifiche tecniche e-service']]")
    EServiceTemplateTechnicalInfoSection technicalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Soglie e attributi']]")
    EServiceTemplateThresholdsAndAttributesSection thresholdsAndAttributesSection();
}

