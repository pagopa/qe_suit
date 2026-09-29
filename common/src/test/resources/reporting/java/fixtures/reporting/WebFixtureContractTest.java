package fixtures.reporting;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class WebFixtureContractTest {

    @TestFactory
    Stream<DynamicTest> shouldResolvePage() {
        return webContractValidator
                .as(user, tenant)
                .on(DebugClientAssertionPage.class)
                .tests(scenarios());
    }
}
