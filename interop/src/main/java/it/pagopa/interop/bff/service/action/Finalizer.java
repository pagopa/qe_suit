package it.pagopa.interop.bff.service.action;

import org.springframework.http.ResponseEntity;

public interface Finalizer {
    default <E> E body(ResponseEntity<E> finalResponse) {
        if (finalResponse != null) return finalResponse.getBody();
        return null;
    }

    default <E> ResponseEntity<E> raw(ResponseEntity<E> finalResponse) {
        return finalResponse;
    }
}
