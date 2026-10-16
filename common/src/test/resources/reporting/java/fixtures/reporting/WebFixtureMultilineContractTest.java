package fixtures.reporting;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class WebFixtureMultilineContractTest {

    @TestFactory
    Stream<DynamicTest> shouldResolvePageMultiline() {
        return webContractValidator
                .as(user, tenant)
                .on(
                        DebugClientAssertionPage.class
                )
                .tests(scenarios());
    }
}
