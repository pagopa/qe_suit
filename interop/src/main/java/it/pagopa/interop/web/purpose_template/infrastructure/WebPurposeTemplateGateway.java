package it.pagopa.interop.web.purpose_template.infrastructure;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateGateway;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCatalogPage;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateGeneralInfoPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebPurposeTemplateGateway implements PurposeTemplateGateway {

    private final PurposeTemplateCatalogPage purposeTemplateCatalogPage;
    private final PurposeTemplateGeneralInfoPage purposeTemplateGeneralInfoPage;

    @Override
    public PurposeTemplate createPurposeTemplate(Tenant creator) {
        purposeTemplateCatalogPage.navigateTo();

        purposeTemplateCatalogPage.openCreationForm();

        // valore di personalData assunto come default (true) in assenza di uno
        // specifico valore atteso dallo scenario; da rivedere quando sarà disponibile
        // un parametro esplicito nel Gherkin.
        purposeTemplateCatalogPage.setPersonalData(true);
        purposeTemplateCatalogPage.confirmCreation();

        return null;
    }

    @Override
    public void assertGeneralInformationPageDisplayed(Tenant creator) {
        purposeTemplateGeneralInfoPage.assertLoaded();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}

