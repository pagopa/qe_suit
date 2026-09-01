package it.pagopa.infrastructure.fuzzing;

import java.util.List;

public interface FuzzEngine {
    List<FuzzCase> generate(Object source);
}
