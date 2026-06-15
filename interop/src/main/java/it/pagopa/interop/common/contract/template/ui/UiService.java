package it.pagopa.interop.common.contract.template.ui;

import it.frontend.e2e.framework.web.domain.Component;

public interface UiService<M, C extends Component> extends UiReader<C, M>, UiWriter<M, C> {

}
