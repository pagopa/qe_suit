package fixtures.reporting;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class WebFixtureInvalidOnArgumentContractTest {

    @TestFactory
    Stream<DynamicTest> shouldFailWhenOnArgumentIsNotClassLiteral() {
        Class<?> page = DebugClientAssertionPage.class;
        return webContractValidator
                .as(user, tenant)
                .on(page)
                .tests(scenarios());
    }
}
