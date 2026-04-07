package it.frontend.e2e.framework.core.capability.dispatcher;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.handler.ICapabilityHandler;
import it.frontend.e2e.framework.core.capability.dispatcher.impl.TestDefaultCapabilityDispatcher;
import it.frontend.e2e.framework.core.model.TestElement;
import it.frontend.e2e.framework.core.model.TestLocation;
import it.frontend.e2e.framework.core.model.TestSelector;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CapabilityDispatcher")
class DefaultCapabilityDispatcherTest {

    private interface ActionCapability extends Gettable<TestSelector, TestLocation, TestElement> {
        void action();
    }

    @Mock
    private ICapabilityHandler firstHandler;

    @Mock
    private ICapabilityHandler secondHandler;

    private TestDefaultCapabilityDispatcher dispatcher;
    private Method actionMethod;
    private TestSelector selector;
    private TestLocation location;
    private CapabilityScope scope;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        mocks = MockitoAnnotations.openMocks(this);
        List<ICapabilityHandler> handlers = new ArrayList<>();
        handlers.add(firstHandler);
        handlers.add(secondHandler);
        selector = new TestSelector();
        location = new TestLocation();
        scope = new CapabilityScope("", "");
        dispatcher = new TestDefaultCapabilityDispatcher(handlers);
        actionMethod = ActionCapability.class.getMethod("action");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    @DisplayName("dovrebbe delegare al primo handler quando puo gestire il method")
    void shouldDelegateToFirstHandlerWhenItCanHandle() {
        TestElement expectedElement = new TestElement(selector, location);
        when(firstHandler.canHandle(actionMethod)).thenReturn(true);
        when(firstHandler.handle(actionMethod, null, scope)).thenReturn(Optional.of(expectedElement));

        Optional<TestElement> result = dispatcher.dispatch(actionMethod, null, scope);

        assertTrue(result.isPresent());
        assertSame(expectedElement, result.get());
        verify(firstHandler).canHandle(actionMethod);
        verify(firstHandler).handle(actionMethod, null, scope);
        verify(secondHandler, never()).handle(actionMethod, null, scope);
    }

    @Test
    @DisplayName("dovrebbe delegare al secondo handler quando il primo non puo gestire")
    void shouldDelegateToSecondHandlerWhenFirstCannotHandle() {
        TestElement expectedElement = new TestElement(selector, location);
        when(firstHandler.canHandle(actionMethod)).thenReturn(false);
        when(secondHandler.canHandle(actionMethod)).thenReturn(true);
        when(secondHandler.handle(actionMethod, null, scope)).thenReturn(Optional.of(expectedElement));

        Optional<TestElement> result = dispatcher.dispatch(actionMethod, null, scope);

        assertTrue(result.isPresent());
        assertSame(expectedElement, result.get());
        verify(firstHandler).canHandle(actionMethod);
        verify(firstHandler, never()).handle(actionMethod, null, scope);
        verify(secondHandler).canHandle(actionMethod);
        verify(secondHandler).handle(actionMethod, null, scope);
    }

    @Test
    @DisplayName("dovrebbe lanciare eccezione quando nessun handler puo gestire il method")
    void shouldThrowExceptionWhenNoHandlerCanHandle() {
        when(firstHandler.canHandle(actionMethod)).thenReturn(false);
        when(secondHandler.canHandle(actionMethod)).thenReturn(false);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> dispatcher.dispatch(actionMethod, null, scope)
        );

        assertTrue(exception.getMessage().contains("No handler for"));
        assertTrue(exception.getMessage().contains(ActionCapability.class.getSimpleName()));
        verify(firstHandler, never()).handle(actionMethod, null, scope);
        verify(secondHandler, never()).handle(actionMethod, null, scope);
    }

    @Test
    @DisplayName("dovrebbe gestire lista vuota di handler")
    void shouldHandleEmptyHandlerList() {
        dispatcher = new TestDefaultCapabilityDispatcher(new ArrayList<>());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> dispatcher.dispatch(actionMethod, null, scope)
        );

        assertTrue(exception.getMessage().contains("No handler for"));
    }

    @Test
    @DisplayName("dovrebbe restituire Optional.empty se l'handler restituisce empty")
    void shouldReturnEmptyOptionalIfHandlerReturnsEmpty() {
        when(firstHandler.canHandle(actionMethod)).thenReturn(true);
        when(firstHandler.handle(actionMethod, null, scope)).thenReturn(Optional.empty());

        Optional<?> result = dispatcher.dispatch(actionMethod, null, scope);

        assertTrue(result.isEmpty());
        verify(firstHandler).handle(actionMethod, null, scope);
    }

    @Test
    @DisplayName("dovrebbe propagare eccezioni dagli handler")
    void shouldPropagateExceptionsFromHandlers() {
        RuntimeException expectedException = new RuntimeException("handler error");
        when(firstHandler.canHandle(actionMethod)).thenReturn(true);
        when(firstHandler.handle(actionMethod, null, scope)).thenThrow(expectedException);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> dispatcher.dispatch(actionMethod, null, scope)
        );

        assertEquals(expectedException, exception);
        verify(firstHandler).handle(actionMethod, null, scope);
    }
}
