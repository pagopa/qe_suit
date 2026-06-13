package it.pagopa.interop.web.service.template;

import it.frontend.e2e.framework.web.domain.Component;

public interface UiReader<C extends Component, M> {

    default M read() {
        C component = getComponent();
        component.assertLoaded();

        return mapToModel(component);
    }

    C getComponent();

    M mapToModel(C component);
}
