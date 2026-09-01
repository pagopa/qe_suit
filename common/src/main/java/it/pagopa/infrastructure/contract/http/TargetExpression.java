package it.pagopa.infrastructure.contract.http;

@FunctionalInterface
public interface TargetExpression<T> {
    Object select(T root);
}
