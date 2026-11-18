package it.pagopa.interop.web.riskanalysis.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

import it.frontend.e2e.framework.web.domain.Component;

/**
 * Risk-analysis accordion exposing questions, submitted answers and explanatory notes.
 */
public interface RiskAnalysisCard extends Component {
    @XPath(".//div[contains(@class, 'MuiAccordionSummary-root') and not(contains(@class, 'Mui-expanded'))]")
    Button header();

    @XPath(".//div[contains(@class, 'MuiAccordionSummary-content')]//h6[contains(@class, 'MuiTypography-h6')]")
    Label accordionTitle();

    @XPath(".//section//h3[contains(normalize-space(.), 'Analisi del rischio')]")
    Label title();

    @XPath(".//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]")
    Label purposeQuestionLabel();

    @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]]//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]/following-sibling::p[1]")
    Label purposeAnswerLabel();

    @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]]//p[contains(normalize-space(.), 'Indicare per quale finalità si intende accedere ai dati')]/following-sibling::p[2]")
    Label purposeNoteLabel();

    @XPath(".//p[contains(normalize-space(.), 'Specificare il fine perseguito per fini istituzionali')]")
    Label specificPurposeQuestionLabel();

    @XPath(".//li[.//p[contains(normalize-space(.), 'Specificare il fine perseguito per fini istituzionali')]]//p[contains(normalize-space(.), 'Specificare il fine perseguito per fini istituzionali')]/following-sibling::p[1]")
    Label specificPurposeAnswerLabel();

    @XPath(".//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]")
    Label personalDataQuestionLabel();

    @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]]//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]/following-sibling::p[1]")
    Label personalDataAnswerLabel();

    @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]]//p[contains(normalize-space(.), 'Indicare se si accede a dati personali')]/following-sibling::p[2]")
    Label personalDataNoteLabel();

    @XPath(".//p[contains(normalize-space(.), 'Indicare se si accede a dati non personali di terzi')]")
    Label thirdPartyDataQuestionLabel();

    @XPath(".//li[.//p[contains(normalize-space(.), 'Indicare se si accede a dati non personali di terzi')]]//p[contains(normalize-space(.), 'Indicare se si accede a dati non personali di terzi')]/following-sibling::p[1]")
    Label thirdPartyDataAnswerLabel();
}
