package it.frontend.e2e.framework.core.binder;

import it.frontend.e2e.framework.core.binder.impl.TestDefaultBinder;
import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.model.TestElement;
import it.frontend.e2e.framework.core.model.TestLocation;
import it.frontend.e2e.framework.core.model.TestSelector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AbstractPresentationBinder")
class DefaultBinderTest {

    private final DefaultBinder defaultBinder = new TestDefaultBinder();;

    @Test
    @DisplayName("dovrebbe creare un proxy per un'interfaccia valida")
    void shouldCreateProxyForValidInterface() {
        TestInterface proxy = defaultBinder.bind(TestInterface.class);

        assertTrue(Proxy.isProxyClass(proxy.getClass()));
    }

    @Test
    @DisplayName("dovrebbe lanciare eccezione quando si associa una classe non-interfaccia")
    void shouldThrowExceptionForNonInterfaceClass() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> defaultBinder.bind(ConcreteClass.class)
        );

        assertTrue(exception.getMessage().contains("is not an interface"));
    }

    @Test
    @DisplayName("dovrebbe preservare il tipo dell'interfaccia nel proxy")
    void shouldUseSameTypeAsInterface() {
        TestInterface proxy = defaultBinder.bind(TestInterface.class);

        assertInstanceOf(TestInterface.class, proxy);
    }

    @Test
    @DisplayName("dovrebbe usare lo stesso classloader dell'interfaccia")
    void shouldUseSameClassLoaderAsInterface() {
        TestInterface proxy = defaultBinder.bind(TestInterface.class);
        ClassLoader interfaceClassLoader = TestInterface.class.getClassLoader();

        assertEquals(interfaceClassLoader, proxy.getClass().getClassLoader());
    }

    @Test
    @DisplayName("dovrebbe associare più interfacce indipendentemente")
    void shouldBindMultipleInterfacesIndependently() {
        FirstInterface firstProxy = defaultBinder.bind(FirstInterface.class);
        SecondInterface secondProxy = defaultBinder.bind(SecondInterface.class);

        assertTrue(Proxy.isProxyClass(firstProxy.getClass()));
        assertTrue(Proxy.isProxyClass(secondProxy.getClass()));
        assertNotEquals(firstProxy.getClass(), secondProxy.getClass());
    }

    @Test
    @DisplayName("dovrebbe gestire interfaccia null")
    void shouldThrowExceptionWhenInterfaceIsNull() {
        assertThrows(NullPointerException.class, () -> defaultBinder.bind(null));
    }

    interface TestInterface extends Gettable<TestSelector, TestLocation, TestElement> {
        void testMethod();
    }

    interface FirstInterface extends Gettable<TestSelector, TestLocation, TestElement> {
        void firstMethod();
    }

    interface SecondInterface extends Gettable<TestSelector, TestLocation, TestElement> {
        void secondMethod();
    }

    static class ConcreteClass implements Gettable<TestSelector, TestLocation, TestElement> {
        void concreteMethod() {}

        @Override
        public Optional<TestElement> get() {
            return Optional.empty();
        }
    }
}

