package it.pagopa.interop.domain.web.commons.component;

import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

public interface Button extends Component, Clickable, Gettable, Readable<String> {
}
