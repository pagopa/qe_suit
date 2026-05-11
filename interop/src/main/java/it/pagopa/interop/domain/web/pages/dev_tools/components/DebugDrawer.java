package it.pagopa.interop.domain.web.pages.dev_tools.components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.interop.domain.web.commons.component.Chip;
import it.pagopa.interop.domain.web.commons.component.Drawer;

@XPath("//div[contains(@class, 'MuiDrawer-root')][1]")
public interface DebugDrawer extends Drawer {

    @XPath(".//h6")
    Readable<String> title();

    @XPath(".//h6/following-sibling::p[1]")
    Readable<String> description();

    Chip result();

    @XPath(".//ul/li[1]//p")
    Readable<String> errorCode();
}
