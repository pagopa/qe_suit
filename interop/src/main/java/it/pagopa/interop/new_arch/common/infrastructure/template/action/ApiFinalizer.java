package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;

public interface ApiFinalizer<Response> {
    BaseActionContext getContext();

    ApiResponse getRawResponse();

    @SuppressWarnings("unchecked")
    default Response getResponse() {
        return (Response) getRawResponse().as(getContext().getResponseClass());
    }
}
