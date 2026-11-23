package it.pagopa.infrastructure.http.restassured;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AcceptLanguageFilterTest {

    @Test
    void sendsItalianLanguage() {
        assertThat(apply(new AcceptLanguageFilter(() -> Locale.ITALIAN), new RequestSpecBuilder().build()))
                .isEqualTo("it");
    }

    @Test
    void sendsEnglishLanguage() {
        assertThat(apply(new AcceptLanguageFilter(() -> Locale.ENGLISH), new RequestSpecBuilder().build()))
                .isEqualTo("en");
    }

    @Test
    void defaultsToItalianWhenContextHasNoLanguage() {
        assertThat(apply(new AcceptLanguageFilter(() -> null), new RequestSpecBuilder().build()))
                .isEqualTo("it");
    }

    @Test
    void replacesExistingHeaderInsteadOfDuplicatingIt() {
        var spec = new RequestSpecBuilder().addHeader("Accept-Language", "fr").build();
        assertThat(apply(new AcceptLanguageFilter(() -> Locale.ENGLISH), spec)).isEqualTo("en");
        var request = (FilterableRequestSpecification) spec;
        assertThat(request.getHeaders().getList("Accept-Language")).hasSize(1);
    }

    @Test
    void readsContextOnEveryRequest() {
        AtomicReference<Locale> language = new AtomicReference<>(Locale.ITALIAN);
        AcceptLanguageFilter filter = new AcceptLanguageFilter(language::get);
        assertThat(apply(filter, new RequestSpecBuilder().build())).isEqualTo("it");
        language.set(Locale.ENGLISH);
        assertThat(apply(filter, new RequestSpecBuilder().build())).isEqualTo("en");
    }

    @Test
    void separateParallelContextsDoNotShareLanguage() throws Exception {
        ThreadLocal<Locale> language = ThreadLocal.withInitial(() -> Locale.ITALIAN);
        AcceptLanguageFilter filter = new AcceptLanguageFilter(language::get);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);
        try {
            Callable<String> italian = () -> {
                language.set(Locale.ITALIAN);
                ready.countDown();
                if (!start.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("timeout");
                try { return apply(filter, new RequestSpecBuilder().build()); }
                finally { language.remove(); }
            };
            Callable<String> english = () -> {
                language.set(Locale.ENGLISH);
                ready.countDown();
                if (!start.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("timeout");
                try { return apply(filter, new RequestSpecBuilder().build()); }
                finally { language.remove(); }
            };
            Future<String> it = executor.submit(italian);
            Future<String> en = executor.submit(english);
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            assertThat(it.get(5, TimeUnit.SECONDS)).isEqualTo("it");
            assertThat(en.get(5, TimeUnit.SECONDS)).isEqualTo("en");
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    private static String apply(AcceptLanguageFilter filter, io.restassured.specification.RequestSpecification spec) {
        FilterableRequestSpecification request = (FilterableRequestSpecification) spec;
        FilterableResponseSpecification response = mock(FilterableResponseSpecification.class);
        FilterContext context = mock(FilterContext.class);
        Response result = mock(Response.class);
        when(context.next(request, response)).thenReturn(result);
        assertThat(filter.filter(request, response, context)).isSameAs(result);
        verify(context).next(request, response);
        return request.getHeaders().getValue("Accept-Language");
    }
}
