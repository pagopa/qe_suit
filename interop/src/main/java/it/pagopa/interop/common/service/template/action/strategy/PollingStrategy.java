package it.pagopa.interop.common.service.template.action.strategy;

import org.springframework.http.HttpStatusCode;

import java.util.function.Predicate;

@FunctionalInterface
public interface PollingStrategy<T> {

    boolean isSatisfied(HttpStatusCode statusCode, T body);

    PollingStrategy<Object> UNTIL_SUCCESS = (status, body) -> status.is2xxSuccessful();

    PollingStrategy<Object> UNTIL_ERROR = (status, body) -> !status.is2xxSuccessful();

    static <E> PollingStrategy<E> UNTIL_SUCCESS_WHERE(Predicate<E> bodyCondition) {
        return (status, body) -> status.is2xxSuccessful() && body != null && bodyCondition.test(body);
    }
}