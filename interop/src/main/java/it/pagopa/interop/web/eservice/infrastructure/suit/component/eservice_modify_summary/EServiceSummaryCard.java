package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.suit.component.Card;
import it.pagopa.infrastructure.suit.component.Label;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//*[contains(@class,'MuiAccordion-root')]")
public interface EServiceSummaryCard extends Card {

    @XPath(".//*[contains(@class,'MuiAccordionSummary-root')]")
    Header header();

    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    Content content();

    default void expand() {
        String expanded = header().get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Intestazione della card non presente"))
                .getAttributes().get("aria-expanded");
        if (!"true".equals(expanded) && !"false".equals(expanded)) {
            throw new IllegalStateException("Stato aria-expanded della card non valido: " + expanded);
        }
        if ("false".equals(expanded)) {
            header().click();
        }
    }

    interface Header extends Component, Clickable {
        @XPath(".//h6[contains(@class, 'MuiTypography-h6')]")
        Label title();

        @XPath(".//h6[contains(@class, 'MuiTypography-subtitle2')]")
        Label subtitle();
    }

    interface Content extends Component {
    }

    interface Field extends Component {
        @XPath("./div[1]/p")
        Label label();

        @XPath("./div[2]/p")
        Label value();
    }

}