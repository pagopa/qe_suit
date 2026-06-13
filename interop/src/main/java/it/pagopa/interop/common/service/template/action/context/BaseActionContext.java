package it.pagopa.interop.common.service.template.action.context;

import it.pagopa.interop.common.domain.model.TestModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class BaseActionContext<Entity, Model extends TestModel> {
    private final Supplier<ResponseEntity<Entity>> responseSupplier;
    private final Function<Entity, List<Model>> mapper;
}
