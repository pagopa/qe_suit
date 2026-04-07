package it.frontend.e2e.framework.core.capability.handler;

import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.model.TestElement;
import it.frontend.e2e.framework.core.model.TestLocation;
import it.frontend.e2e.framework.core.model.TestSelector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AbstractCapabilityHandler")
class AbstractCapabilityHandlerTest {

    private interface TestCapability extends Gettable<TestSelector, TestLocation, TestElement> {
        void action();
        void anotherAction();
    }

    private interface DifferentCapability extends Gettable<TestSelector, TestLocation, TestElement> {
        void differentAction();
    }

    // Interfaccia che estende TestCapability
    private interface ExtendedTestCapability extends TestCapability {
        void extendedAction();
    }

    // Interfaccia che non ha nulla a che fare con TestCapability
    private interface UnrelatedCapability extends Gettable<TestSelector, TestLocation, TestElement> {
        void unrelatedAction();
    }

    // Helper methods per creare mock delle capabilities
    private static TestCapability createMockTestCapability() {
        return new TestCapability() {
            @Override
            public void action() {}
            @Override
            public void anotherAction() {}
            @Override
            public Optional<TestElement> get() { return Optional.empty(); }
        };
    }

    private static DifferentCapability createMockDifferentCapability() {
        return new DifferentCapability() {
            @Override
            public void differentAction() {}
            @Override
            public Optional<TestElement> get() { return Optional.empty(); }
        };
    }

    private static ExtendedTestCapability createMockExtendedTestCapability() {
        return new ExtendedTestCapability() {
            @Override
            public void action() {}
            @Override
            public void anotherAction() {}
            @Override
            public void extendedAction() {}
            @Override
            public Optional<TestElement> get() { return Optional.empty(); }
        };
    }

    private static UnrelatedCapability createMockUnrelatedCapability() {
        return new UnrelatedCapability() {
            @Override
            public void unrelatedAction() {}
            @Override
            public Optional<TestElement> get() { return Optional.empty(); }
        };
    }

    private static class TestCapabilityHandler extends AbstractCapabilityHandler<TestCapability> {

        protected TestCapabilityHandler(TestCapability capabilityImpl) {
            super(capabilityImpl);
        }

        @Override
        public <T> T handle(Method method, Object[] args, CapabilityScope scope) {
            return null;
        }
    }

    private static class DifferentCapabilityHandler extends AbstractCapabilityHandler<DifferentCapability> {

        protected DifferentCapabilityHandler(DifferentCapability capabilityImpl) {
            super(capabilityImpl);
        }

        @Override
        public <T> T handle(Method method, Object[] args, CapabilityScope scope) {
            return null;
        }
    }

    private static class ExtendedTestCapabilityHandler extends AbstractCapabilityHandler<ExtendedTestCapability> {

        protected ExtendedTestCapabilityHandler(ExtendedTestCapability capabilityImpl) {
            super(capabilityImpl);
        }

        @Override
        public <T> T handle(Method method, Object[] args, CapabilityScope scope) {
            return null;
        }
    }

    private static class UnrelatedCapabilityHandler extends AbstractCapabilityHandler<UnrelatedCapability> {

        protected UnrelatedCapabilityHandler(UnrelatedCapability capabilityImpl) {
            super(capabilityImpl);
        }

        @Override
        public <T> T handle(Method method, Object[] args, CapabilityScope scope) {
            return null;
        }
    }

    private TestCapabilityHandler handler;

    @Nested
    @DisplayName("Test canHandle - Branch capabilityClass.isAssignableFrom(dc)")
    class FirstBranchTests {

        @Test
        @DisplayName("dovrebbe gestire metodo dalla classe capability dichiarata (dc == capabilityClass)")
        void shouldHandleMethodFromDeclaredCapability() throws NoSuchMethodException {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = TestCapability.class.getMethod("action");
            // dc = TestCapability, capabilityClass = TestCapability
            // capabilityClass.isAssignableFrom(dc) = true

            assertTrue(handler.canHandle(method));
        }

        @Test
        @DisplayName("dovrebbe NON gestire metodo da interfaccia che estende la capability dichiarata")
        void shouldHandleMethodFromExtendedCapability() throws NoSuchMethodException {
            TestCapabilityHandler handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = ExtendedTestCapability.class.getMethod("extendedAction");
            // dc = ExtendedTestCapability, capabilityClass = TestCapability
            // capabilityClass.isAssignableFrom(dc) = true (TestCapability è assignable da ExtendedTestCapability)
            // MA: declaringClass != capabilityClass, quindi ritorna false
            // Il handler gestisce solo metodi dichiarati nella capabilityClass stessa

            assertFalse(handler.canHandle(method));
        }

        @Test
        @DisplayName("dovrebbe gestire metodo ereditato da interfaccia estesa")
        void shouldHandleInheritedMethodFromExtendedCapability() throws NoSuchMethodException {
            TestCapabilityHandler handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = ExtendedTestCapability.class.getMethod("action");
            // Metodo action() è ereditato da TestCapability, ma il declaringClass rimane TestCapability
            // dc = TestCapability, capabilityClass = TestCapability
            // capabilityClass.isAssignableFrom(dc) = true

            assertTrue(handler.canHandle(method));
        }
    }

    @Nested
    @DisplayName("Test canHandle - Branch dc.isAssignableFrom(capabilityClass)")
    class SecondBranchTests {

        @Test
        @DisplayName("dovrebbe gestire quando capabilityClass è assignable dal declaringClass")
        void shouldHandleWhenCapabilityClassIsAssignableFromDeclaringClass() throws NoSuchMethodException {
            ExtendedTestCapabilityHandler handler = new ExtendedTestCapabilityHandler(createMockExtendedTestCapability());
            Method method = TestCapability.class.getMethod("action");
            // dc = TestCapability, capabilityClass = ExtendedTestCapability
            // capabilityClass.isAssignableFrom(dc) = false
            // dc.isAssignableFrom(capabilityClass) = true (TestCapability è assignable da ExtendedTestCapability)

            assertTrue(handler.canHandle(method));
        }
    }

    @Nested
    @DisplayName("Test canHandle - Caso FALSE (entrambi i branch falsi)")
    class BothBranchesFalseTests {

        @Test
        @DisplayName("dovrebbe rifiutare metodo da capability completamente diversa")
        void shouldRejectMethodFromCompletelyDifferentCapability() throws NoSuchMethodException {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = DifferentCapability.class.getMethod("differentAction");
            // dc = DifferentCapability, capabilityClass = TestCapability
            // capabilityClass.isAssignableFrom(dc) = false (DifferentCapability non è assignable da TestCapability)
            // dc.isAssignableFrom(capabilityClass) = false (TestCapability non è assignable da DifferentCapability)

            assertFalse(handler.canHandle(method));
        }

        @Test
        @DisplayName("dovrebbe rifiutare metodo da capability non correlata")
        void shouldRejectMethodFromUnrelatedCapability() throws NoSuchMethodException {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = UnrelatedCapability.class.getMethod("unrelatedAction");
            // dc = UnrelatedCapability, capabilityClass = TestCapability
            // Nessuno dei due branch è true

            assertFalse(handler.canHandle(method));
        }
    }

    @Nested
    @DisplayName("Test canHandle - Edge cases e combinazioni")
    class EdgeCasesTests {

        @Test
        @DisplayName("dovrebbe distinguere tra handler per capability diverse")
        void shouldDistinguishBetweenDifferentCapabilityHandlers() throws NoSuchMethodException {
            TestCapabilityHandler firstHandler = new TestCapabilityHandler(createMockTestCapability());
            DifferentCapabilityHandler secondHandler = new DifferentCapabilityHandler(createMockDifferentCapability());
            Method testCapabilityMethod = TestCapability.class.getMethod("action");
            Method differentCapabilityMethod = DifferentCapability.class.getMethod("differentAction");

            assertTrue(firstHandler.canHandle(testCapabilityMethod));
            assertFalse(firstHandler.canHandle(differentCapabilityMethod));
            assertFalse(secondHandler.canHandle(testCapabilityMethod));
            assertTrue(secondHandler.canHandle(differentCapabilityMethod));
        }

        @Test
        @DisplayName("dovrebbe gestire correttamente handler per la stessa capability")
        void shouldHandleMultipleHandlersForSameCapability() throws NoSuchMethodException {
            TestCapabilityHandler handler1 = new TestCapabilityHandler(createMockTestCapability());
            TestCapabilityHandler handler2 = new TestCapabilityHandler(createMockTestCapability());
            Method method = TestCapability.class.getMethod("action");

            assertTrue(handler1.canHandle(method));
            assertTrue(handler2.canHandle(method));
        }

        @Test
        @DisplayName("dovrebbe usare l'uguaglianza della classe per determinare se può gestire")
        void shouldUseExactClassEqualityForHandling() throws NoSuchMethodException {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = TestCapability.class.getMethod("action");
            Class<?> methodDeclaringClass = method.getDeclaringClass();
            Class<?> expectedCapabilityClass = TestCapability.class;

            assertEquals(expectedCapabilityClass, methodDeclaringClass);
            assertTrue(handler.canHandle(method));
        }

        @Test
        @DisplayName("NON dovrebbe gestire metodo con declaring class che è sottoclasse della capability")
        void shouldHandleMethodWithDeclaredClassAsSubclass() throws NoSuchMethodException {
            TestCapabilityHandler handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = ExtendedTestCapability.class.getMethod("extendedAction");
            // ExtendedTestCapability extends TestCapability
            // dc = ExtendedTestCapability, capabilityClass = TestCapability
            // capabilityClass.isAssignableFrom(dc) = true
            // MA: declaringClass != capabilityClass, quindi il metodo non è gestito

            assertFalse(handler.canHandle(method));
        }
    }

    @Nested
    @DisplayName("Test metodi specifici della capability")
    class SpecificMethodsTests {

        @Test
        @DisplayName("dovrebbe riconoscere un metodo della capability dichiarata")
        void shouldRecognizeMethodFromDeclaredCapability() throws NoSuchMethodException {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = TestCapability.class.getMethod("action");

            assertTrue(handler.canHandle(method));
        }

        @Test
        @DisplayName("dovrebbe riconoscere un metodo diverso della stessa capability")
        void shouldRecognizeAnyMethodFromDeclaredCapability() throws NoSuchMethodException {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = TestCapability.class.getMethod("anotherAction");

            assertTrue(handler.canHandle(method));
        }

        @Test
        @DisplayName("dovrebbe rifiutare un metodo di una capability diversa")
        void shouldRejectMethodFromDifferentCapability() throws NoSuchMethodException {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method method = DifferentCapability.class.getMethod("differentAction");

            assertFalse(handler.canHandle(method));
        }

        @Test
        @DisplayName("dovrebbe rifiutare metodo null")
        void shouldRejectNullMethod() {
            handler = new TestCapabilityHandler(createMockTestCapability());
            Method nullMethod = null;

            assertFalse(handler.canHandle(nullMethod));
        }
    }

}
