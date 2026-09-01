package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.objectgraph.ObjectGraphQuery;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MockitoObjectGraphQueryResolverTest {
    private final MockitoObjectGraphQueryResolver resolver = new MockitoObjectGraphQueryResolver();

    @Test
    void resolvesSimpleGetterNavigation() {
        ObjectGraphQuery query = resolver.resolve(Payload.class, Payload::getName);
        assertEquals(ObjectGraphQuery.root().property(method(Payload.class, "getName")), query);
    }

    @Test
    void resolvesNestedListNavigationWithDifferentIndices() {
        ObjectGraphQuery first = resolver.resolve(Payload.class, p -> p.getContacts().get(0).getEmail());
        ObjectGraphQuery second = resolver.resolve(Payload.class, p -> p.getContacts().get(2).getEmail());
        assertNotEquals(first, second);
    }

    @Test
    void mapNavigationFailsFastWithExplicitMessage() {
        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> resolver.resolve(PayloadWithMap.class, p -> p.getMetadata().get("x"))
        );
        assertEquals("Map key navigation is not supported by ObjectGraphQuery", exception.getMessage());
    }

    @Test
    void arbitraryMethodFailsFast() {
        assertThrows(ContractHttpException.class, () -> resolver.resolve(Payload.class, Payload::computeValue));
    }

    @Test
    void sequentialResolutionsAreIndependent() {
        ObjectGraphQuery first = resolver.resolve(Payload.class, p -> p.getContacts().get(0).getEmail());
        ObjectGraphQuery second = resolver.resolve(Payload.class, Payload::getName);
        assertNotEquals(first, second);
    }

    @Test
    void concurrentResolutionsDoNotShareState() throws Exception {
        var executor = Executors.newFixedThreadPool(4);
        try {
            Callable<ObjectGraphQuery> first = () -> resolver.resolve(Payload.class, p -> p.getContacts().get(0).getEmail());
            Callable<ObjectGraphQuery> second = () -> resolver.resolve(Payload.class, p -> p.getContacts().get(1).getEmail());
            Future<ObjectGraphQuery> f1 = executor.submit(first);
            Future<ObjectGraphQuery> f2 = executor.submit(second);
            assertNotEquals(f1.get(), f2.get());
        } finally {
            executor.shutdownNow();
        }
    }

    private static java.lang.reflect.Method method(Class<?> type, String name) {
        try {
            return type.getMethod(name);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    static class Payload {
        public String getName() {
            return "name";
        }

        public List<Contact> getContacts() {
            return List.of();
        }

        public String computeValue() {
            return "x";
        }
    }

    static class Contact {
        public String getEmail() {
            return "a@b";
        }
    }

    static class PayloadWithMap {
        public Map<String, String> getMetadata() {
            return Map.of();
        }
    }
}
