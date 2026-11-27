package it.pagopa.interop.web.purpose.infrastructure.page.purpose_edit_steps;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;

@XPath("//form[.//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Informazioni generali']]]")
public interface GeneralInfoStep extends Component {

    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Informazioni generali']]")
    GeneralInfoCard generalInfoCard();

    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Stima chiamate API']]")
    ApiCallsEstimateCard apiCallsEstimateCard();

    interface GeneralInfoCard extends Card {

        @XPath(".//h2[contains(@class, 'MuiTypography-h6') and normalize-space()='Informazioni generali']")
        Label sectionTitle();

        @XPath(".//div[contains(@class, 'MuiFormControl-root') and .//input[@id='title']]")
        TextField titleInput();

        @XPath(".//div[contains(@class, 'MuiFormControl-root') and .//textarea[@id='description' and not(@aria-hidden='true')]]")
        TextField descriptionInput();

        Switch freeOfChargeSwitch();

        @XPath(".//*[@id='isFreeOfCharge-label']")
        Label freeOfChargeLabel();

        @XPath(".//div[contains(@class, 'MuiFormControl-root') and .//textarea[@id='freeOfChargeReason' and not(@aria-hidden='true')]]")
        TextField freeOfChargeReasonInput();
    }

    interface ApiCallsEstimateCard extends Card {

        @XPath(".//h2[contains(@class, 'MuiTypography-h6') and normalize-space()='Stima chiamate API']")
        Label sectionTitle();

        @XPath(".//h2/following::p[contains(@class, 'MuiTypography-body2')][1]")
        Label sectionSubtitle();

        @XPath(".//div[contains(@class, 'MuiFormControl-root') and .//input[@id='dailyCalls']]")
        TextField dailyCallsInput();

        @XPath(".//div[@role='alert' and contains(@class, 'MuiAlert-colorSuccess')]")
        AvailableCallsAlert availableCallsAlert();
    }

    interface AvailableCallsAlert extends Alert {

        @XPath(".//div[contains(@class, 'MuiAlertTitle-root')]")
        Label alertTitle();

        @XPath(".//div[contains(@class, 'MuiStack-root')]/div[contains(@class, 'MuiStack-root')][1]/p[1]")
        Label perConsumerLabel();

        @XPath(".//div[contains(@class, 'MuiStack-root')]/div[contains(@class, 'MuiStack-root')][1]/p[2]")
        Label perConsumerValueLabel();

        @XPath(".//div[contains(@class, 'MuiStack-root')]/div[contains(@class, 'MuiStack-root')][2]/p[1]")
        Label totalLabel();

        @XPath(".//div[contains(@class, 'MuiStack-root')]/div[contains(@class, 'MuiStack-root')][2]/p[2]")
        Label totalValueLabel();

        @XPath(".//span[contains(@class, 'MuiTypography-caption')]")
        Label disclaimerNote();
    }
}
