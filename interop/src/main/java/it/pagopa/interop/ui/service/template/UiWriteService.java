package it.pagopa.interop.ui.service.template;

import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.interop.ui.domain.request.UiRequest;
import it.pagopa.interop.common.service.template.RequestOverride;

public interface UiWriteService<R extends UiRequest, C extends Component, M> extends UiReadService<C, M> {

    default M fill(RequestOverride<R> overrides) {
        R request = doDefaultRequest();
        overrides.applyTo(request);

        C component = getComponent();
        component.assertLoaded();

        doFill(request);
        afterFill(request);

        return mapToModel(component);
    }

    default M fill() {
        return fill(_req -> {});
    }

    R doDefaultRequest();

    void doFill(R request);

    default void afterFill(R request) {
    }
}
