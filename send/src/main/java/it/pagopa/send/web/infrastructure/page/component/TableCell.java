package it.pagopa.send.web.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

@XPath(".//td[contains(@class,'MuiTableCell-root')]")
public interface TableCell extends Readable<String>,Component {
    @XPath(".//div[contains(@class,'MuiBox-root')]")
    Readable<String> value();
}
