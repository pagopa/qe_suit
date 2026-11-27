package it.pagopa.interop.web.purpose.infrastructure.page.purpose_edit_steps;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Card;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.RadioButton;

@XPath("//form[.//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Assegnazione']]]")
public interface AssignmentStep extends Component {

    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Assegnazione']]")
    AssignmentCard assignmentCard();

    interface AssignmentCard extends Card {

        @XPath(".//h2[normalize-space()='Assegnazione']")
        Label title();

        @XPath(".//h2[normalize-space()='Assegnazione']/following::p[contains(@class, 'MuiTypography-body2')][1]")
        Label description();

        @XPath(".//legend[contains(@class, 'MuiFormLabel-root') and contains(normalize-space(), 'Scegli una modalità')]")
        Label reviewModeLegend();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='reviewMode' and @value='ADMIN_WRITES_ADMIN_SIGNS']]")
        RadioButton adminWritesAdminSignsRadio();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='reviewMode' and @value='ADMIN_WRITES_REVIEWER_SIGNS']]")
        RadioButton adminWritesReviewerSignsRadio();

        @XPath(".//label[contains(@class, 'MuiFormControlLabel-root') and .//input[@type='radio' and @name='reviewMode' and @value='REVIEWER_WRITES_REVIEWER_SIGNS']]")
        RadioButton reviewerWritesReviewerSignsRadio();
    }


}
