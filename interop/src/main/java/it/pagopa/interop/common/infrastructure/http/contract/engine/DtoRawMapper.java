package it.pagopa.interop.common.infrastructure.http.contract.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

final class DtoRawMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DtoRawMapper() {
    }

    static Map<String, Object> toRawMap(Object dto) {
        try {
            Map<String, Object> rawMap = OBJECT_MAPPER.convertValue(dto, new TypeReference<>() {
            });
            return rawMap != null ? rawMap : new HashMap<>();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Impossibile convertire il DTO valido in mappa raw", e);
        }
    }
}