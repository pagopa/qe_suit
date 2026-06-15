package it.pagopa.interop.common.cucumber.context;

import it.pagopa.interop.common.contract.model.TestModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContextEntry<Model extends TestModel> {
    private final Model item;
    private final String alias;
}
