package it.pagopa.interop.ui.service.template;

import it.frontend.e2e.framework.web.domain.Component;

public interface UiService<M, C extends Component> extends UiReadService<C, M>, UiWriteService<M, C> {

}
