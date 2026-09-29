package fixtures.reporting;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class WebFixtureNoOnContractTest {

    @TestFactory
    Stream<DynamicTest> shouldFailWhenNoOn() {
        return webContractValidator
                .as(user, tenant)
                .tests(scenarios());
    }
}
