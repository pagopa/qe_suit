package it.pagopa.interop.common.infrastructure.http.contract.engine;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

final class DtoRawMapper {

    private DtoRawMapper() {
    }

    static Map<String, Object> toRawMap(Object dto) {
        Map<String, Object> rawMap = new HashMap<>();

        for (Field field : dto.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                rawMap.put(field.getName(), field.get(dto));
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Impossibile leggere il campo [%s] dal DTO valido".formatted(field.getName()),
                        e
                );
            }
        }

        return rawMap;
    }
}