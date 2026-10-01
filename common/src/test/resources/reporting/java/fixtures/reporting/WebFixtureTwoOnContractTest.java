package fixtures.reporting;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class WebFixtureTwoOnContractTest {

    @TestFactory
    Stream<DynamicTest> shouldFailWhenTwoOnCalls() {
        return webContractValidator
                .as(user, tenant)
                .on(FirstPage.class)
                .on(SecondPage.class)
                .tests(scenarios());
    }
}
