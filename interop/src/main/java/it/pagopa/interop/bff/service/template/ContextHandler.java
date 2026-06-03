package it.pagopa.interop.bff.service.template;

import org.springframework.http.ResponseEntity;

public interface ContextHandler<Entity> {
    void doUpdateModelContext(Entity model);
    void doUpdateHttpContext(ResponseEntity<Entity> entity);
}
