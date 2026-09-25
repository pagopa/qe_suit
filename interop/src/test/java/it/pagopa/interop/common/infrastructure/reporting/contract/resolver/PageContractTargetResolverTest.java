package it.pagopa.interop.common.infrastructure.reporting.contract.resolver;

import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractChannelConfig;
import it.pagopa.interop.common.infrastructure.reporting.contract.config.ContractTargetType;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageContractTargetResolverTest {

    private static final ContractChannelConfig WEB = new ContractChannelConfig("web", "WEB", "Web", ContractTargetType.PAGE, null);
    private static final Path SOURCE_ROOT = Path.of("src/test/resources/reporting/java");

    @Test
    void resolvesPageClassFromOnCall() {
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        String target = resolver.resolveTarget(new ContractFactoryContext(WEB, "fixtures.reporting.WebFixtureContractTest", "shouldResolvePage"));
        assertEquals("DebugClientAssertionPage", target);
    }

    @Test
    void resolvesPageClassFromMultilineOnCall() {
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        String target = resolver.resolveTarget(new ContractFactoryContext(WEB, "fixtures.reporting.WebFixtureMultilineContractTest", "shouldResolvePageMultiline"));
        assertEquals("DebugClientAssertionPage", target);
    }

    @Test
    void failsWhenNoOnCallExists() {
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        assertThrows(IllegalStateException.class, () -> resolver.resolveTarget(new ContractFactoryContext(
                WEB, "fixtures.reporting.WebFixtureNoOnContractTest", "shouldFailWhenNoOn"
        )));
    }

    @Test
    void failsWhenTwoOnCallsExist() {
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        assertThrows(IllegalStateException.class, () -> resolver.resolveTarget(new ContractFactoryContext(
                WEB, "fixtures.reporting.WebFixtureTwoOnContractTest", "shouldFailWhenTwoOnCalls"
        )));
    }

    @Test
    void failsWhenOnArgumentIsNotClassLiteral() {
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        assertThrows(IllegalStateException.class, () -> resolver.resolveTarget(new ContractFactoryContext(
                WEB, "fixtures.reporting.WebFixtureInvalidOnArgumentContractTest", "shouldFailWhenOnArgumentIsNotClassLiteral"
        )));
    }

    @Test
    void failsWhenMethodDoesNotExist() {
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        assertThrows(IllegalStateException.class, () -> resolver.resolveTarget(new ContractFactoryContext(
                WEB, "fixtures.reporting.WebFixtureContractTest", "missingMethod"
        )));
    }

    @Test
    void failsWhenSourceDoesNotExist() {
        PageContractTargetResolver resolver = new PageContractTargetResolver(SOURCE_ROOT);
        assertThrows(IllegalStateException.class, () -> resolver.resolveTarget(new ContractFactoryContext(
                WEB, "fixtures.reporting.MissingClass", "any"
        )));
    }
}
