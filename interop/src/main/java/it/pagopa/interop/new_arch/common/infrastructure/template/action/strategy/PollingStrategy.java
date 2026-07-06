package it.pagopa.interop.new_arch.common.infrastructure.template.action.strategy;

@FunctionalInterface
public interface PollingStrategy<Response> {

    boolean isSatisfied(int statusCode, Response body);

    PollingStrategy<Object> UNTIL_SUCCESS = (code, body) -> is2xxSuccessful(code);

    PollingStrategy<Object>  UNTIL_ERROR = (code, body) -> !is2xxSuccessful(code);

    static <R> PollingStrategy<R> UNTIL_SUCCESS_WHERE(java.util.function.Predicate<R> bodyCondition) {
        return (code, body) -> is2xxSuccessful(code) && bodyCondition.test(body);
    }

    private static boolean is2xxSuccessful(int code) {
        return code >= 200 && code < 300;
    }
}