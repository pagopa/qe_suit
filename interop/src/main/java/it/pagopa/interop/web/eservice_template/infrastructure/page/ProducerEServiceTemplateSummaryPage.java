package it.pagopa.interop.web.eservice_template.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplatePublishThankYouPage;
import it.pagopa.utils.async.PollingUtils;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}/modifica/riepilogo")
public interface ProducerEServiceTemplateSummaryPage extends Page {

    @XPath(".//button[normalize-space()='Pubblica']")
    Button publishButton();

    PublishTemplateConfirmDialog publishConfirmDialog();

    ProducerEServiceTemplatePublishThankYouPage publishThankYouPage();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> publishButton().get(Gettable.FindPolicy.PRESENT).isPresent(),
                Boolean::booleanValue,
                "Producer E-Service Template Summary Page is not loaded yet. Publish button is not present"
        );
    }

    @XPath("//div[contains(@class,'MuiDialog-paper')]")
    interface PublishTemplateConfirmDialog extends Component {

        @XPath(".//*[self::h2 or contains(@class,'MuiDialogTitle-root')]")
        Readable<String> title();

        @XPath(".//*[contains(@class,'MuiDialogContent-root') or contains(@class,'MuiDialogContentText-root')]")
        Readable<String> description();

        @XPath(".//button[normalize-space()='Pubblica']")
        Button publishButton();

        @Override
        default void assertLoaded() {
            PollingUtils.pollUntil(
                    () -> publishButton().get(Gettable.FindPolicy.CLICKABLE).isPresent(),
                    Boolean::booleanValue,
                    "Publish template confirmation dialog is not loaded yet"
            );
        }
    }
}

