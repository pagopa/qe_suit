package it.pagopa.interop.ui.service.template;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.domain.request.UiRequest;

public interface UiService<R extends UiRequest, C extends Component, M> extends UiReadService<C, M>, UiWriteService<R, C, M> {

}
