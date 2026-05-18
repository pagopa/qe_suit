package it.pagopa.send.domain.web.pages.mittente;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.model.NotificationData;
import org.assertj.core.api.Assertions;

@Url("${url.notifiche.mittente.new-notification}#selfCareToken=${token.mittente}")
public interface CreateNotificationPage extends Page {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid='step-submit']")
    Clickable continueButton();

    interface InformazioniPreliminariStep extends Component{
        @XPath(".//*[@id=\"subject\"]")
        Writable<String> subject();

        @XPath(".//input[@id='abstract']")
        Writable<String> description();

        @XPath(".//input[@id='paProtocolNumber']")
        Writable<String> protocolNumber();

        @XPath(".//input[@id='taxonomyCode']")
        Writable<String> taxonomyCode();

        // Group combobox — clicks to open the dropdown
        @XPath(".//*[@id='group']")
        Clickable openGroupDropdown();

        default void fillFields(NotificationData data) {
            //selectLanguageItalian().click();
            subject().write(data.getSubject());
            protocolNumber().write(data.getProtocolNumber());
            taxonomyCode().write(data.getTaxonomyCode());
            //selectModel890().click();
            openGroupDropdown().click();
        }

        default XPathSelector groupOptionSelector(String groupName) {
            return XPathSelector.of(
                    "//*[@role='option'][normalize-space()='" + groupName + "']"
            );
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    InformazioniPreliminariStep informazioniPreliminariStep();

    interface DestinatariStep extends Component{
        // Recipient type
        @XPath(".//*[@id='recipient-pf']")
        Clickable selectNaturalPerson();

        @XPath(".//*[@id='recipient-pg']")
        Clickable selectLegalPerson();

        // Main fields
        @XPath(".//input[@id='recipients[0].taxId']")
        Writable<String> taxId();

        @XPath(".//input[@id='recipients[0].firstName']")
        Writable<String> firstName();

        @XPath(".//input[@id='recipients[0].lastName']")
        Writable<String> lastName();

        // Address lookup
        @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][1]")
        Clickable selectNationalRegistry();

        @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][2]")
        Clickable selectManualAddress();

        // PEC — optional
        @XPath(".//input[@id='recipients[0].digitalDomicile']")
        Writable<String> pecAddress();

        default void fillFields(NotificationData data) {
            selectNaturalPerson().click();
            taxId().write(data.getTaxId());
            firstName().write(data.getFirstName());
            lastName().write(data.getLastName());
            selectNationalRegistry().click();
            if (data.getPec() != null && !data.getPec().isEmpty()) {
                pecAddress().write(data.getPec());
            }
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DestinatariStep destinatariStep();

    interface DettaglioPosizioneDebitoriaStep extends Component{

        @XPath(".//label[@data-testid='paymentModel'][1]")
        Clickable selectPagoPa();

        @XPath(".//label[@data-testid='paymentModel'][2]")
        Clickable selectF24();

        @XPath(".//label[@data-testid='paymentModel'][3]")
        Clickable selectPagoPaAndF24();

        @XPath(".//label[@data-testid='paymentModel'][4]")
        Clickable selectNoPayment();

        default void selectPaymentTypeAndSubmit(String paymentType) {
            switch (paymentType) {
                case "PAGO_PA"       -> selectPagoPa().click();
                case "F24"           -> selectF24().click();
                case "PAGO_PA_F24"   -> selectPagoPaAndF24().click();
                case "NOTHING"       -> selectNoPayment().click();
                default -> throw new IllegalArgumentException(
                        "Unknown payment type: " + paymentType
                );
            }
        }

        default void fillFields(NotificationData data) {
            selectPaymentTypeAndSubmit(data.getPaymentType());
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DettaglioPosizioneDebitoriaStep dettaglioPosizioneDebitoriaStep();

    interface DocumentazioneStep extends Component{

        @XPath(".//input[@id='documents.0.name']")
        Writable<String> documentTitle();

        @XPath(".//*[@id=\"file-input\"]")
        Uploadable attachment();

        default void fillFields(NotificationData data) {
            documentTitle().write(data.getDocumentTitle());
            attachment().upload(data.getDocumentFilePath());
        }
    }

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div/div/form")
    DocumentazioneStep documentazioneStep();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
            Assertions.assertThat(h).isIn("Nuova notifica", "New notification");
        });
    }

    default void compileInformazioniPreliminari(NotificationData data) {
       informazioniPreliminariStep().fillFields(data);
    }

    default void compileDestinatari(NotificationData data) {
        destinatariStep().fillFields(data);
    }

    default void compileDettaglioPosizioneDebitoria(NotificationData data) {
        dettaglioPosizioneDebitoriaStep().fillFields(data);
    }

    default void compileDocumentazione(NotificationData data) {
        documentazioneStep().fillFields(data);
    }
}
