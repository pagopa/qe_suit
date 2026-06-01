package it.pagopa.interop.ui.service.template;

import it.frontend.e2e.framework.web.domain.Component;

public interface UiReadService<C extends Component, M> {

    default M read() {
        C component = getComponent();
        component.assertLoaded();

        return mapToModel(component);
    }

    C getComponent();

    M mapToModel(C component);
}
