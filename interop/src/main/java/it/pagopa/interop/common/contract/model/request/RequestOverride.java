package it.pagopa.interop.common.contract.model.request;

@FunctionalInterface
public interface RequestOverride<Request> {
    void applyTo(Request request);
}