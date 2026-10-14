package it.pagopa.infrastructure.reporting.contract.resolver.fixtures;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Fixture contract class scanned by ContractTargetRegressionTest.
 * The single .on(...) call is what PageContractTargetResolver extracts from the source file.
 */
public class WebFixturePageContractTest {

    @TestFactory
    Stream<DynamicTest> shouldValidateFixturePage() {
        return validator()
                .on(FixturePage.class)
                .tests();
    }

    private static PageStage validator() {
        return new PageStage();
    }

    static class FixturePage {
    }

    static class PageStage {

        PageStage on(Class<?> pageClass) {
            return this;
        }

        Stream<DynamicTest> tests() {
            return Stream.empty();
        }
    }
}
