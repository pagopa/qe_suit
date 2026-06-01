package it.pagopa.interop.common.service.template;

@FunctionalInterface
public interface RequestOverride<Request> {
    void applyTo(Request request);
}