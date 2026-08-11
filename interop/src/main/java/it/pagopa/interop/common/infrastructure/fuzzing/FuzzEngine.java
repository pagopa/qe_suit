package it.pagopa.interop.common.infrastructure.fuzzing;

import java.util.List;

public interface FuzzEngine {
    List<FuzzCase> generate(Object source);
}
