package it.pagopa.infrastructure.contract.http;

import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface HttpAuthorizationStages {

    interface ApiCallsStage {
        ExpectationsStage apiCalls(List<? extends Supplier<?>> operations);

        ExpectationsStage apiCalls(Supplier<?>... operations);
    }

    interface ExpectationsStage {
        /**
         * Status HTTP atteso per tutte le combinazioni; se non indicato vale {@code 403}.
         */
        ExpectationsStage expectedStatus(int status);

        Stream<DynamicTest> tests();
    }
}
