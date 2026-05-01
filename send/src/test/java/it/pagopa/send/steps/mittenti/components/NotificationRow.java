package it.pagopa.send.steps.mittenti.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath("//*[@id='notificationsTable.body.row']")
public interface NotificationRow extends Component {

    @XPath("./td[3]")
    Readable<String> iun();

    @XPath("./td[2]")
    Readable<String> recipient();
}
