package it.pagopa.interop.common.contract.request;

@FunctionalInterface
public interface RequestOverride<Request> {
    void applyTo(Request request);
}