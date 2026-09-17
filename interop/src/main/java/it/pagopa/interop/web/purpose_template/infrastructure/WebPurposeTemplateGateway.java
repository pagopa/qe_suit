package it.pagopa.interop.web.purpose_template.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateGateway;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateCatalogPage;
import it.pagopa.interop.web.purpose_template.infrastructure.page.PurposeTemplateGeneralInfoPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebPurposeTemplateGateway implements PurposeTemplateGateway {

    private final PurposeTemplateCatalogPage purposeTemplateCatalogPage;
    private final PurposeTemplateGeneralInfoPage purposeTemplateGeneralInfoPage;
    private final EntityStore entityStore;

    @Override
    public PurposeTemplate createPurposeTemplate(Tenant creator) {
        purposeTemplateCatalogPage.navigateTo();

        purposeTemplateCatalogPage.openCreationForm();

        // valore di personalData assunto come default (true) in assenza di uno
        // specifico valore atteso dallo scenario; da rivedere quando sarà disponibile
        // un parametro esplicito nel Gherkin.
        purposeTemplateCatalogPage.setPersonalData(true);
        purposeTemplateCatalogPage.confirmCreation();

        UUID purposeTemplateId = purposeTemplateCatalogPage.currentTemplateIdFromUrl();

        PurposeTemplate purposeTemplate = PurposeTemplate.builder()
                .id(purposeTemplateId)
                .creatorId(creator.getOrganizationId())
                .build();

        entityStore.upsert(purposeTemplate);

        return purposeTemplate;
    }

    @Override
    public void assertGeneralInformationPageDisplayed(Tenant creator) {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);

        String currentUrl = purposeTemplateCatalogPage.getCurrentBrowserUrl();
        if (!currentUrl.contains(purposeTemplate.getId().toString())) {
            throw new AssertionError(
                    "L'URL corrente (" + currentUrl + ") non corrisponde al Template di Finalità creato (id="
                            + purposeTemplate.getId() + ")"
            );
        }

        purposeTemplateGeneralInfoPage.assertLoaded();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}

