package it.pagopa.interop.common.infrastructure.http.contract.engine;

import it.pagopa.interop.common.infrastructure.http.contract.FuzzVectors;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FuzzerGenerator {

    private static final int REQUIRED_FIELD_MISSING_STATUS = 400;

    record FuzzedCase(
            String fieldName,
            FuzzVectors.FuzzId fuzzId,
            Object fuzzedValue,
            int expectedStatus,
            String description,
            Map<String, Object> rawBody
    ) {
    }

    static List<FuzzedCase> generateFuzzCases(Object validDto) {
        List<FuzzedCase> fuzzCases = new ArrayList<>();

        Field[] fields = validDto.getClass().getDeclaredFields();
        Map<String, Object> baseMap = DtoRawMapper.toRawMap(validDto);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }

            String fieldName = field.getName();
            if (!baseMap.containsKey(fieldName)) {
                continue;
            }

            Class<?> fieldType = field.getType();

            /*
             * Assunzione:
             * ogni campo presente nel DTO valido è required.
             *
             * Quindi, oltre ai valori fuzzati, generiamo sempre anche
             * il caso in cui il campo è completamente assente dal JSON.
             */
            injectMissingRequiredField(fuzzCases, baseMap, fieldName);

            if (fieldType.equals(String.class)) {
                injectVectors(fuzzCases, baseMap, fieldName, FuzzVectors.STRINGS);
            } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                injectVectors(fuzzCases, baseMap, fieldName, FuzzVectors.INTEGERS);
            } else if (fieldType.equals(java.util.UUID.class)) {
                injectVectors(fuzzCases, baseMap, fieldName, FuzzVectors.UUIDS);
                injectVectors(fuzzCases, baseMap, fieldName, FuzzVectors.STRINGS);
            }
        }

        return fuzzCases;
    }

    private static void injectMissingRequiredField(List<FuzzedCase> fuzzCases,
                                                   Map<String, Object> baseMap,
                                                   String fieldName) {
        Map<String, Object> fuzzedMap = new HashMap<>(baseMap);

        /*
         * Qui non mettiamo il campo a null.
         * Lo rimuoviamo proprio dalla mappa, quindi dal JSON finale.
         */
        fuzzedMap.remove(fieldName);

        fuzzCases.add(new FuzzedCase(
                fieldName,
                FuzzVectors.FuzzId.REQUIRED_MISSING,
                "<ABSENT>",
                REQUIRED_FIELD_MISSING_STATUS,
                "Campo required assente dal payload",
                fuzzedMap
        ));
    }

    private static void injectVectors(List<FuzzedCase> fuzzCases,
                                      Map<String, Object> baseMap,
                                      String fieldName,
                                      List<? extends FuzzVectors<?>> vectors) {
        for (FuzzVectors<?> vector : vectors) {
            Map<String, Object> fuzzedMap = new HashMap<>(baseMap);

            /*
             * Sostituiamo solo il campo sotto test.
             * Tutti gli altri campi restano validi.
             */
            fuzzedMap.put(fieldName, vector.value());

            fuzzCases.add(new FuzzedCase(
                    fieldName,
                    vector.id(),
                    vector.value(),
                    vector.expectedStatus(),
                    vector.description(),
                    fuzzedMap
            ));
        }
    }
}