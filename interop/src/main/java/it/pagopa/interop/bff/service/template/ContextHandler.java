package it.pagopa.interop.bff.service.template;

public interface ContextHandler<Entity> {
    void updateContext(Entity entity);
}
