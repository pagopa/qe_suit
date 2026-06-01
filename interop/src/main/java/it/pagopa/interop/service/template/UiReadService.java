package it.pagopa.interop.service.template;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.domain.model.Model;

public interface UiReadService<C extends Component, M extends Model> {

    default M read() {
        C component = getComponent();
        component.assertLoaded();

        return mapToModel(component);
    }

    C getComponent();

    M mapToModel(C component);
}
