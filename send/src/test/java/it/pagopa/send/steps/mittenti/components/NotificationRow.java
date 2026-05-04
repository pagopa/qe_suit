package it.pagopa.send.steps.mittenti.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

@XPath("//*[@id='notificationsTable.body.row']")
public interface NotificationRow extends Component {

    @XPath("./td[4]")
    Readable<WebPresentationElement> iun();

    @XPath("./td[2]")
    Readable<WebPresentationElement> recipient();

    @XPath("./td[1]")
    Readable<WebPresentationElement> date();

    @XPath("./td[6]")
    Readable<WebPresentationElement> status();
}
