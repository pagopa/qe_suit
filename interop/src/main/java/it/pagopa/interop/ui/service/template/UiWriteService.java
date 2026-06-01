package it.pagopa.interop.ui.service.template;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.common.service.template.RequestOverride;

public interface UiWriteService<M, C extends Component> extends UiReadService<C, M> {

    default M fill(RequestOverride<M> overrides) {
        M model = doDefaultModel();
        overrides.applyTo(model);

        C component = getComponent();
        component.assertLoaded();

        doFill(model);
        afterFill(model);

        return mapToModel(component);
    }

    default M fill() {
        return fill(_req -> {
        });
    }

    M doDefaultModel();

    void doFill(M model);

    default void afterFill(M model) {
    }
}
