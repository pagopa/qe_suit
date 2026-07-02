package it.pagopa.interop.common.contract.template.action.context;

import it.pagopa.interop.common.contract.model.Identifiable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class BaseActionContext<Entity, Model extends Identifiable> {
    private final Supplier<ResponseEntity<Entity>> responseSupplier;
    private final Function<Entity, List<Model>> mapper;
}
