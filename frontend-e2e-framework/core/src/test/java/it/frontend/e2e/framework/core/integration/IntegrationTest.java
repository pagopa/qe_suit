package it.frontend.e2e.framework.core.integration;

import it.frontend.e2e.framework.core.binder.impl.TestDefaultBinder;
import it.frontend.e2e.framework.core.capability.core.TestCapability;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IntegrationTest")
class IntegrationTest {

    private final TestDefaultBinder testBinder = new TestDefaultBinder();

    @Test
    @DisplayName("dovrebbe essere possibile il binding di una capability ed invoke un metodo con tipo di ritorno void")
    void shouldBindCapabilityAndInvokeVoidMethod() {
        TestCapability capability = testBinder.bind(TestCapability.class);

        assertNotNull(capability);
        assertInstanceOf(TestCapability.class, capability);
        assertTrue(Proxy.isProxyClass(capability.getClass()));
        assertDoesNotThrow(capability::action);
    }

    @Test
    @DisplayName("dovrebbe essere possibile il binding di una capability ed invoke un metodo con tipo di ritorno List")
    void shouldBindCapabilityAndInvokeListMethod() {
        TestCapability capability = testBinder.bind(TestCapability.class);

        assertNotNull(capability);
        assertInstanceOf(TestCapability.class, capability);
        assertTrue(Proxy.isProxyClass(capability.getClass()));

        Object result = capability.getList();

        assertNotNull(result);
        assertInstanceOf(List.class, result);
    }

    @Test
    @DisplayName("dovrebbe essere possibile il binding di una capability ed invoke un metodo con tipo di ritorno Optional")
    void shouldBindCapabilityAndInvokeOptionalMethod() {
        TestCapability capability = testBinder.bind(TestCapability.class);

        assertNotNull(capability);
        assertInstanceOf(TestCapability.class, capability);
        assertTrue(Proxy.isProxyClass(capability.getClass()));

        Object result = capability.getOptional();

        assertNotNull(result);
        assertInstanceOf(Optional.class, result);
    }
}
