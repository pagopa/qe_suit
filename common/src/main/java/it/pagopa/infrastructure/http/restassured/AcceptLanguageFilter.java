package it.pagopa.infrastructure.http.restassured;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/** Adds the scenario language to each HTTP request (RFC 9110, Accept-Language). */
public final class AcceptLanguageFilter implements Filter {

    private final Supplier<Locale> languageSupplier;

    public AcceptLanguageFilter(Supplier<Locale> languageSupplier) {
        this.languageSupplier = Objects.requireNonNull(languageSupplier, "languageSupplier must not be null");
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {
        Locale language = languageSupplier.get();
        if (language == null || language.getLanguage().isBlank()) {
            language = Locale.ITALIAN;
        }
        // Set an authoritative value rather than allowing duplicate Accept-Language headers.
        requestSpec.replaceHeader("Accept-Language", language.toLanguageTag());
        return context.next(requestSpec, responseSpec);
    }
}
