package it.pagopa.interop.new_arch.common.infrastructure.template.action;

import com.fasterxml.jackson.core.type.TypeReference;
import it.pagopa.interop.new_arch.common.infrastructure.http.ApiResponse;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.context.BaseActionContext;
import it.pagopa.interop.new_arch.common.kernel.domain.Identifiable;

import java.util.List;

public interface Finalizer<Response, Model extends Identifiable> {
    BaseActionContext getContext();

    ApiResponse getRawResponse();

    @SuppressWarnings("unchecked")
    default Response getResponse(){
        return (Response) getRawResponse().as(getContext().getResponseClass());
    }

    @SuppressWarnings("unchecked")
    default Model getModel(){
        return (Model) getRawResponse().as(getContext().getModelClass());
    }

    default List<Model> getModels(){
        return getRawResponse().as(new TypeReference<>() {
        });
    }
}
