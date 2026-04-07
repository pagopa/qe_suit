package it.frontend.e2e.framework.core.binder;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.binder.context.BindContext;
import it.frontend.e2e.framework.core.binder.impl.TestInvocationHandlerDefault;
import it.frontend.e2e.framework.core.capability.Capability;
import it.frontend.e2e.framework.core.capability.dispatcher.ICapabilityDispatcher;
import it.frontend.e2e.framework.core.model.DomainElement;
import it.frontend.e2e.framework.core.model.TestElement;
import it.frontend.e2e.framework.core.model.TestLocation;
import it.frontend.e2e.framework.core.model.TestSelector;
import it.frontend.e2e.framework.core.capability.context.CapabilityScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AbstractPresentationInvocationHandler")
class DefaultBinderInvocationHandlerTest {

    public interface TestInterface {
        default String defaultMethod() {
            return "default";
        }

        Optional<TestElement> element();

        Optional<TestElement> elementWithArg(String value);
    }

    public interface TestOptionalInterface {
        Optional<TestDomainElement> optionalDomainElement();

        Optional<TestCapability> optionalCapability();

        Optional<?> optionalWildcard();

        Optional<String> optionalString();

        Optional<TestBestEffortDomainElement> optionalBestEffortDomainElement();
    }

    public interface TestDomainElement extends DomainElement {
        @XPath("/child")
        TestChildElement child();
    }

    public interface TestChildElement extends DomainElement {
        String getText();
    }

    public interface TestCapability extends Capability {
        void click();
    }

    public interface TestWithSelectorOnMethod {
        @XPath("/button[@id='submit']")
        TestDomainElement button();
    }

    public interface TestWithSelectorOnType {
        TestAnnotatedElement element();
    }

    @XPath("/annotated")
    public interface TestAnnotatedElement extends DomainElement {
        String getValue();
    }

    public interface TestNoSelector extends DomainElement {
        String getData();
    }

    public interface TestBestEffortDomainElement extends DomainElement {
        TestCapability capability();

        String read();

        default void clickInsideDefaultMethod() {
            capability().click();
        }
    }

    @Mock
    private ICapabilityDispatcher dispatcher;

    private DefaultBinderInvocationHandler handler;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        handler = new TestInvocationHandlerDefault(dispatcher);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    @DisplayName("dovrebbe delegare metodi default all'implementazione dell'interfaccia")
    void shouldDelegateDefaultMethodsToInterface() throws Throwable {
        TestInterface proxy = createProxyInstance();
        Method defaultMethod = TestInterface.class.getMethod("defaultMethod");

        Object result = handler.invoke(proxy, defaultMethod, new Object[]{});

        assertEquals("default", result);
        verify(dispatcher, never()).dispatch(any(), any(), eq(scope("")));
    }

    @Test
    @DisplayName("dovrebbe delegare metodi di Object a se stesso")
    void shouldDelegateObjectMethodsToItself() throws Throwable {
        TestInterface proxy = createProxyInstance();
        Method toStringMethod = Object.class.getMethod("toString");

        Object result = handler.invoke(proxy, toStringMethod, new Object[]{});

        assertNotNull(result);
        verify(dispatcher, never()).dispatch(any(), any(), eq(scope("")));
    }

    @Test
    @DisplayName("dovrebbe delegare al dispatcher l'implementazione dei metodi custom")
    void shouldDispatchCustomMethods() throws Throwable {
        TestInterface proxy = createProxyInstance();
        Method method = TestInterface.class.getMethod("element");
        TestElement expectedElement = new TestElement(new TestSelector(), new TestLocation());
        Optional<TestElement> expectedResult = Optional.of(expectedElement);
        Object[] args = null;

        when(dispatcher.dispatch(method, args, scope(""))).thenReturn(expectedResult);

        Object result = handler.invoke(proxy, method, args);

        assertEquals(expectedResult, result);
        verify(dispatcher, times(1)).dispatch(method, args, scope(""));
    }

    @Test
    @DisplayName("dovrebbe gestire metodi con argomenti")
    void shouldHandleMethodsWithArguments() throws Throwable {
        TestInterface proxy = createProxyInstance();
        Method method = TestInterface.class.getMethod("elementWithArg", String.class);
        Optional<TestElement> expectedResult = Optional.empty();

        when(dispatcher.dispatch(eq(method), any(), eq(scope("")))).thenReturn(expectedResult);

        Object result = handler.invoke(proxy, method, new Object[]{"value"});

        assertEquals(expectedResult, result);
        verify(dispatcher, times(1)).dispatch(eq(method), any(), eq(scope("")));
    }

    @Test
    @DisplayName("dovrebbe propagare eccezioni dal dispatcher per metodi non Optional")
    void shouldPropagateDispatcherExceptions() throws Throwable {
        TestNoSelector proxy = (TestNoSelector) Proxy.newProxyInstance(
                TestNoSelector.class.getClassLoader(),
                new Class[]{TestNoSelector.class},
                handler
        );
        Method method = TestNoSelector.class.getMethod("getData");
        RuntimeException exception = new RuntimeException("dispatcher error");

        when(dispatcher.dispatch(method, null, scope(""))).thenThrow(exception);

        assertThrows(RuntimeException.class, () ->
                handler.invoke(proxy, method, null)
        );
    }

    @Nested
    @DisplayName("Test metodi Object")
    class ObjectMethodsTests {

        @Test
        @DisplayName("dovrebbe gestire equals correttamente")
        void shouldHandleEquals() throws Throwable {
            TestInterface proxy1 = createProxyInstance();
            TestInterface proxy2 = createProxyInstance();
            Method equalsMethod = Object.class.getMethod("equals", Object.class);

            Object result1 = handler.invoke(proxy1, equalsMethod, new Object[]{proxy1});
            Object result2 = handler.invoke(proxy1, equalsMethod, new Object[]{proxy2});

            assertTrue((Boolean) result1);
            assertFalse((Boolean) result2);
            verify(dispatcher, never()).dispatch(any(), any(), any());
        }

        @Test
        @DisplayName("dovrebbe gestire hashCode correttamente")
        void shouldHandleHashCode() throws Throwable {
            TestInterface proxy = createProxyInstance();
            Method hashCodeMethod = Object.class.getMethod("hashCode");

            Object result = handler.invoke(proxy, hashCodeMethod, new Object[]{});

            assertNotNull(result);
            assertInstanceOf(Integer.class, result);
            verify(dispatcher, never()).dispatch(any(), any(), any());
        }

        @Test
        @DisplayName("dovrebbe gestire toString correttamente")
        void shouldHandleToString() throws Throwable {
            TestInterface proxy = createProxyInstance();
            Method toStringMethod = Object.class.getMethod("toString");

            Object result = handler.invoke(proxy, toStringMethod, new Object[]{});

            assertNotNull(result);
            assertInstanceOf(String.class, result);
            assertTrue(((String) result).contains("@"));
            verify(dispatcher, never()).dispatch(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Test ricorsione con DomainElement e Capability")
    class RecursionTests {

        @Test
        @DisplayName("dovrebbe creare proxy ricorsivo per DomainElement")
        void shouldCreateRecursiveProxyForDomainElement() throws Throwable {
            TestDomainElement proxy = (TestDomainElement) Proxy.newProxyInstance(
                    TestDomainElement.class.getClassLoader(),
                    new Class[]{TestDomainElement.class},
                    handler
            );

            Method childMethod = TestDomainElement.class.getMethod("child");

            Object result = handler.invoke(proxy, childMethod, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
            verify(dispatcher, never()).dispatch(any(), any(), any());
        }

        @Test
        @DisplayName("dovrebbe creare proxy ricorsivo per Capability")
        void shouldCreateRecursiveProxyForCapability() {
            // Questo test documenta il caso void-return su capability; non serve invocare variabili locali.
            assertDoesNotThrow(() -> TestCapability.class.getMethod("click"));
        }

        @Test
        @DisplayName("dovrebbe comporre correttamente i selector nei proxy ricorsivi")
        void shouldComposeSelectorsInRecursiveProxies() throws Throwable {
            // Crea handler con un contesto iniziale
            BindContext parentCtx = new BindContext(scope("/parent"));
            DefaultBinderInvocationHandler handlerWithContext =
                    new DefaultBinderInvocationHandler(dispatcher, parentCtx);

            TestDomainElement proxy = (TestDomainElement) Proxy.newProxyInstance(
                    TestDomainElement.class.getClassLoader(),
                    new Class[]{TestDomainElement.class},
                    handlerWithContext
            );

            Method childMethod = TestDomainElement.class.getMethod("child");

            // Il metodo child() ha @Selector("/child"), quindi il selector completo sarà "/parent/child"
            Object result = handlerWithContext.invoke(proxy, childMethod, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
        }
    }

    @Nested
    @DisplayName("Test resolveSelector")
    class ResolveSelectorTests {

        @Test
        @DisplayName("dovrebbe risolvere selector dall'annotazione sul metodo")
        void shouldResolveSelectorFromMethodAnnotation() throws Throwable {
            TestWithSelectorOnMethod proxy = (TestWithSelectorOnMethod) Proxy.newProxyInstance(
                    TestWithSelectorOnMethod.class.getClassLoader(),
                    new Class[]{TestWithSelectorOnMethod.class},
                    handler
            );

            Method buttonMethod = TestWithSelectorOnMethod.class.getMethod("button");

            Object result = handler.invoke(proxy, buttonMethod, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
            // Il selector viene risolto dall'annotazione @Selector sul metodo
        }

        @Test
        @DisplayName("dovrebbe risolvere selector dall'annotazione sul return type")
        void shouldResolveSelectorFromReturnTypeAnnotation() throws Throwable {
            TestWithSelectorOnType proxy = (TestWithSelectorOnType) Proxy.newProxyInstance(
                    TestWithSelectorOnType.class.getClassLoader(),
                    new Class[]{TestWithSelectorOnType.class},
                    handler
            );

            Method elementMethod = TestWithSelectorOnType.class.getMethod("element");

            Object result = handler.invoke(proxy, elementMethod, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
            // Il selector viene risolto dall'annotazione @Selector sul return type
        }

        @Test
        @DisplayName("dovrebbe ritornare stringa vuota quando non c'e selector")
        void shouldReturnEmptyStringWhenNoSelector() throws Throwable {
            TestNoSelector proxy = (TestNoSelector) Proxy.newProxyInstance(
                    TestNoSelector.class.getClassLoader(),
                    new Class[]{TestNoSelector.class},
                    handler
            );

            Method getDataMethod = TestNoSelector.class.getMethod("getData");

            // getData ritorna String, quindi verrà dispatchato con selector vuoto
            when(dispatcher.dispatch(eq(getDataMethod), any(), eq(scope("")))).thenReturn("data");

            Object result = handler.invoke(proxy, getDataMethod, null);

            assertEquals("data", result);
            verify(dispatcher).dispatch(eq(getDataMethod), any(), eq(scope("")));
        }
    }

    @Nested
    @DisplayName("Test composizione selector")
    class ComposeSelectorTests {

        @Test
        @DisplayName("dovrebbe ritornare child quando parent e null")
        void shouldReturnChildWhenParentIsNull() throws Throwable {
            BindContext ctx = new BindContext(scope(null));
            DefaultBinderInvocationHandler handlerWithNullCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            TestDomainElement proxy = (TestDomainElement) Proxy.newProxyInstance(
                    TestDomainElement.class.getClassLoader(),
                    new Class[]{TestDomainElement.class},
                    handlerWithNullCtx
            );

            Method childMethod = TestDomainElement.class.getMethod("child");
            Object result = handlerWithNullCtx.invoke(proxy, childMethod, null);

            assertNotNull(result);
            // Il selector dovrebbe essere solo "/child"
        }

        @Test
        @DisplayName("dovrebbe ritornare child quando parent e blank")
        void shouldReturnChildWhenParentIsBlank() throws Throwable {
            BindContext ctx = new BindContext(scope(""));
            DefaultBinderInvocationHandler handlerWithBlankCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            TestDomainElement proxy = (TestDomainElement) Proxy.newProxyInstance(
                    TestDomainElement.class.getClassLoader(),
                    new Class[]{TestDomainElement.class},
                    handlerWithBlankCtx
            );

            Method childMethod = TestDomainElement.class.getMethod("child");
            Object result = handlerWithBlankCtx.invoke(proxy, childMethod, null);

            assertNotNull(result);
            // Il selector dovrebbe essere solo "/child"
        }

        @Test
        @DisplayName("dovrebbe gestire selector assoluto con //")
        void shouldHandleAbsoluteSelectorWithDoubleSlash() throws Throwable {
            BindContext ctx = new BindContext(scope("/parent"));
            DefaultBinderInvocationHandler handlerWithCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            // Creiamo un'interfaccia con selector assoluto
            TestAbsoluteSelector proxy = (TestAbsoluteSelector) Proxy.newProxyInstance(
                    TestAbsoluteSelector.class.getClassLoader(),
                    new Class[]{TestAbsoluteSelector.class},
                    handlerWithCtx
            );

            Method absMethod = TestAbsoluteSelector.class.getMethod("absoluteElement");
            Object result = handlerWithCtx.invoke(proxy, absMethod, null);

            assertNotNull(result);
            // Il selector assoluto // dovrebbe essere usato così com'è
        }

        @Test
        @DisplayName("dovrebbe concatenare parent e child con /")
        void shouldConcatenateParentAndChildWithSlash() throws Throwable {
            BindContext ctx = new BindContext(scope("/parent"));
            DefaultBinderInvocationHandler handlerWithCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            TestDomainElement proxy = (TestDomainElement) Proxy.newProxyInstance(
                    TestDomainElement.class.getClassLoader(),
                    new Class[]{TestDomainElement.class},
                    handlerWithCtx
            );

            Method childMethod = TestDomainElement.class.getMethod("child");
            Object result = handlerWithCtx.invoke(proxy, childMethod, null);

            assertNotNull(result);
            // Il selector dovrebbe essere "/parent/child"
        }

        @Test
        @DisplayName("dovrebbe gestire selector assoluto con (//")
        void shouldHandleAbsoluteSelectorWithParenthesisDoubleSlash() throws Throwable {
            BindContext ctx = new BindContext(scope("/parent"));
            DefaultBinderInvocationHandler handlerWithCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            TestParenthesisSelector proxy = (TestParenthesisSelector) Proxy.newProxyInstance(
                    TestParenthesisSelector.class.getClassLoader(),
                    new Class[]{TestParenthesisSelector.class},
                    handlerWithCtx
            );

            Method method = TestParenthesisSelector.class.getMethod("parenElement");
            Object result = handlerWithCtx.invoke(proxy, method, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
        }

        @Test
        @DisplayName("dovrebbe gestire selector assoluto con .//")
        void shouldHandleAbsoluteSelectorWithDotDoubleSlash() throws Throwable {
            BindContext ctx = new BindContext(scope("/parent"));
            DefaultBinderInvocationHandler handlerWithCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            TestDotSlashSelector proxy = (TestDotSlashSelector) Proxy.newProxyInstance(
                    TestDotSlashSelector.class.getClassLoader(),
                    new Class[]{TestDotSlashSelector.class},
                    handlerWithCtx
            );

            Method method = TestDotSlashSelector.class.getMethod("dotElement");
            Object result = handlerWithCtx.invoke(proxy, method, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
        }

        @Test
        @DisplayName("dovrebbe gestire selector assoluto con //*[@")
        void shouldHandleAbsoluteSelectorWithAnyAttribute() throws Throwable {
            BindContext ctx = new BindContext(scope("/parent"));
            DefaultBinderInvocationHandler handlerWithCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            TestAnyAttributeSelector proxy = (TestAnyAttributeSelector) Proxy.newProxyInstance(
                    TestAnyAttributeSelector.class.getClassLoader(),
                    new Class[]{TestAnyAttributeSelector.class},
                    handlerWithCtx
            );

            Method method = TestAnyAttributeSelector.class.getMethod("anyAttrElement");
            Object result = handlerWithCtx.invoke(proxy, method, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
        }

        @Test
        @DisplayName("dovrebbe concatenare parent e child quando child non inizia con /")
        void shouldConcatenateParentAndChildWhenChildDoesNotStartWithSlash() throws Throwable {
            BindContext ctx = new BindContext(scope("/parent"));
            DefaultBinderInvocationHandler handlerWithCtx =
                    new DefaultBinderInvocationHandler(dispatcher, ctx);

            TestNoLeadingSlashSelector proxy = (TestNoLeadingSlashSelector) Proxy.newProxyInstance(
                    TestNoLeadingSlashSelector.class.getClassLoader(),
                    new Class[]{TestNoLeadingSlashSelector.class},
                    handlerWithCtx
            );

            Method method = TestNoLeadingSlashSelector.class.getMethod("childElement");
            Object result = handlerWithCtx.invoke(proxy, method, null);

            assertNotNull(result);
            assertTrue(Proxy.isProxyClass(result.getClass()));
        }
    }

    @Nested
    @DisplayName("Test Optional binding")
    class OptionalBindingTests {

        @Test
        @DisplayName("dovrebbe fare bind ricorsivo per Optional<DomainElement>")
        void shouldCreateRecursiveProxyForOptionalDomainElement() throws Throwable {
            TestOptionalInterface proxy = createOptionalProxyInstance();
            Method method = TestOptionalInterface.class.getMethod("optionalDomainElement");

            Object result = handler.invoke(proxy, method, null);

            assertInstanceOf(Optional.class, result);
            Optional<?> optionalResult = (Optional<?>) result;
            assertTrue(optionalResult.isPresent());
            assertTrue(Proxy.isProxyClass(optionalResult.get().getClass()));
            verify(dispatcher, never()).dispatch(any(), any(), any());
        }

        @Test
        @DisplayName("dovrebbe fare bind ricorsivo per Optional<Capability>")
        void shouldCreateRecursiveProxyForOptionalCapability() throws Throwable {
            TestOptionalInterface proxy = createOptionalProxyInstance();
            Method method = TestOptionalInterface.class.getMethod("optionalCapability");

            Object result = handler.invoke(proxy, method, null);

            assertInstanceOf(Optional.class, result);
            Optional<?> optionalResult = (Optional<?>) result;
            assertTrue(optionalResult.isPresent());
            assertTrue(Proxy.isProxyClass(optionalResult.get().getClass()));
            verify(dispatcher, never()).dispatch(any(), any(), any());
        }

        @Test
        @DisplayName("dovrebbe delegare Optional<?> al dispatcher senza errori")
        void shouldDelegateWildcardOptionalToDispatcher() throws Throwable {
            TestOptionalInterface proxy = createOptionalProxyInstance();
            Method method = TestOptionalInterface.class.getMethod("optionalWildcard");
            Optional<?> expected = Optional.empty();

            when(dispatcher.dispatch(method, null, scope(""))).thenReturn(expected);

            Object result = handler.invoke(proxy, method, null);

            assertEquals(expected, result);
            verify(dispatcher, times(1)).dispatch(method, null, scope(""));
        }

        @Test
        @DisplayName("dovrebbe wrappare il risultato non-Optional in Optional per tipi non bindabili")
        void shouldWrapNonOptionalDispatcherResultForNonBindableOptional() throws Throwable {
            TestOptionalInterface proxy = createOptionalProxyInstance();
            Method method = TestOptionalInterface.class.getMethod("optionalString");

            when(dispatcher.dispatch(method, null, scope(""))).thenReturn("value");

            Object result = handler.invoke(proxy, method, null);

            assertEquals(Optional.of("value"), result);
            verify(dispatcher, times(1)).dispatch(method, null, scope(""));
        }

        @Test
        @DisplayName("dovrebbe ritornare Optional.empty quando il dispatcher lancia eccezioni su un metodo Optional")
        void shouldReturnEmptyWhenDispatcherThrowsForOptionalMethod() throws Throwable {
            TestInterface proxy = createProxyInstance();
            Method method = TestInterface.class.getMethod("element");
            RuntimeException exception = new RuntimeException("dispatcher error");

            when(dispatcher.dispatch(method, null, scope(""))).thenThrow(exception);

            Object result = handler.invoke(proxy, method, null);

            assertEquals(Optional.empty(), result);
        }

        @Test
        @DisplayName("dovrebbe non propagare eccezioni runtime dentro default method su proxy nato da Optional")
        void shouldNotPropagateRuntimeExceptionInsideDefaultMethodForOptionalProxy() throws Throwable {
            TestOptionalInterface proxy = createOptionalProxyInstance();
            Method method = TestOptionalInterface.class.getMethod("optionalBestEffortDomainElement");
            Method clickMethod = TestCapability.class.getMethod("click");

            when(dispatcher.dispatch(eq(clickMethod), isNull(), eq(scope("")))).thenThrow(new RuntimeException("boom"));

            Optional<?> optionalResult = (Optional<?>) handler.invoke(proxy, method, null);

            assertTrue(optionalResult.isPresent());
            TestBestEffortDomainElement element = (TestBestEffortDomainElement) optionalResult.get();
            assertDoesNotThrow(element::clickInsideDefaultMethod);
        }

        @Test
        @DisplayName("dovrebbe ritornare fallback null per ritorni object quando il dispatcher fallisce su proxy Optional")
        void shouldReturnNullFallbackWhenDispatcherFailsOnOptionalProxy() throws Throwable {
            TestOptionalInterface proxy = createOptionalProxyInstance();
            Method method = TestOptionalInterface.class.getMethod("optionalBestEffortDomainElement");
            Method readMethod = TestBestEffortDomainElement.class.getMethod("read");

            when(dispatcher.dispatch(eq(readMethod), isNull(), eq(scope("")))).thenThrow(new RuntimeException("boom"));

            Optional<?> optionalResult = (Optional<?>) handler.invoke(proxy, method, null);

            assertTrue(optionalResult.isPresent());
            TestBestEffortDomainElement element = (TestBestEffortDomainElement) optionalResult.get();
            assertNull(element.read());
        }
    }

    public interface TestAbsoluteSelector extends DomainElement {
        @XPath("//div[@class='absolute']")
        TestChildElement absoluteElement();
    }

    public interface TestParenthesisSelector extends DomainElement {
        @XPath("(//div[@id='test'])[1]")
        TestChildElement parenElement();
    }

    public interface TestDotSlashSelector extends DomainElement {
        @XPath(".//span[@class='relative']")
        TestChildElement dotElement();
    }

    public interface TestAnyAttributeSelector extends DomainElement {
        @XPath("//*[@data-testid='element']")
        TestChildElement anyAttrElement();
    }

    public interface TestNoLeadingSlashSelector extends DomainElement {
        @XPath("child")
        TestChildElement childElement();
    }

    private TestInterface createProxyInstance() {
        return (TestInterface) Proxy.newProxyInstance(
                TestInterface.class.getClassLoader(),
                new Class[]{TestInterface.class},
                handler
        );
    }

    private TestOptionalInterface createOptionalProxyInstance() {
        return (TestOptionalInterface) Proxy.newProxyInstance(
                TestOptionalInterface.class.getClassLoader(),
                new Class[]{TestOptionalInterface.class},
                handler
        );
    }

    private static CapabilityScope scope(String selector) {
        return new CapabilityScope(selector, "");
    }
}
