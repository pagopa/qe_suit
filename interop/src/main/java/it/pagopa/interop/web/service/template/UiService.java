package it.pagopa.interop.web.service.template;

import it.frontend.e2e.framework.web.domain.Component;

public interface UiService<M, C extends Component> extends UiReader<C, M>, UiWriter<M, C> {

}
