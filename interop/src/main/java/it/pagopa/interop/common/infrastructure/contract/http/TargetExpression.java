package it.pagopa.interop.common.infrastructure.contract.http;

@FunctionalInterface
public interface TargetExpression<T> {
    Object select(T root);
}
