package it.pagopa.interop.common.infrastructure.http.contract.engine;

import it.pagopa.interop.common.infrastructure.http.contract.FuzzVectors;
import it.pagopa.interop.common.infrastructure.http.contract.GetterProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class Fuzz<T extends java.io.Serializable> {

    private static final int DEFAULT_VALID_EXPECTED_STATUS = 200;

    private final T validPayload;
    private final Map<String, Object> validInputs;
    private int validExpectedStatus = DEFAULT_VALID_EXPECTED_STATUS;

    private final FuzzExpectationResolver bodyResolver = new FuzzExpectationResolver();
    private final FuzzExpectationResolver paramsResolver = new FuzzExpectationResolver();
    private final Map<String, java.util.List<FuzzVectors<?>>> customBodyVectorsByField = new java.util.HashMap<>();
    private final Set<String> ignoredBodyFields = new HashSet<>();

    // --- fluent: status valido ---

    public Fuzz<T> expectValid(int expectedStatus) {
        this.validExpectedStatus = expectedStatus;
        return this;
    }

    // --- fluent: regole body ---

    public Fuzz<T> expectBody(GetterProvider<T, ?> getter, FuzzVectors.FuzzId fuzzId, int expectedStatus) {
        bodyResolver.putPrecise(LambdaFieldNameExtractor.fieldNameOf(getter), fuzzId, expectedStatus);
        return this;
    }

    public Fuzz<T> expectBody(FuzzVectors.FuzzId fuzzId, int expectedStatus) {
        bodyResolver.putAttack(fuzzId, expectedStatus);
        return this;
    }

    @SafeVarargs
    public final Fuzz<T> ignoreBody(GetterProvider<T, ?>... getters) {
        Arrays.stream(getters)
                .map(LambdaFieldNameExtractor::fieldNameOf)
                .forEach(ignoredBodyFields::add);
        return this;
    }

    public Fuzz<T> expectBody(GetterProvider<T, ?> getter, FuzzVectors<?> vector) {
        String fieldName = LambdaFieldNameExtractor.fieldNameOf(getter);
        customBodyVectorsByField
                .computeIfAbsent(fieldName, k -> new java.util.ArrayList<>())
                .add(vector);
        return this;
    }

    // --- fluent: regole params ---

    public Fuzz<T> expectParam(String paramName, FuzzVectors.FuzzId fuzzId, int expectedStatus) {
        paramsResolver.putPrecise(paramName, fuzzId, expectedStatus);
        return this;
    }

    public Fuzz<T> expectParam(FuzzVectors.FuzzId fuzzId, int expectedStatus) {
        paramsResolver.putAttack(fuzzId, expectedStatus);
        return this;
    }

    // --- execute ---

    public Stream<DynamicTest> execute(BiConsumer<RequestParts, Integer> apiExecutor) {

        Map<String, Object> validBody = DtoRawMapper.toRawMap(validPayload);

        // 1) test valido
        DynamicTest validTest = dynamicTest(
                FuzzTestNameFormatter.validPayloadName(validExpectedStatus),
                () -> apiExecutor.accept(new RequestParts(validInputs, validBody), validExpectedStatus)
        );

        // 2) params fuzz (body fisso = valido)
        Stream<DynamicTest> paramsFuzzTests = ParamFuzzerGenerator
                .generateFuzzCases(validInputs)
                .stream()
                .map(c -> {
                    int expected = paramsResolver.resolveParam(c.paramName(), c.fuzzId(), c.expectedStatus());
                    return dynamicTest(
                            FuzzTestNameFormatter.paramFuzzName(c, expected),
                            () -> apiExecutor.accept(new RequestParts(c.rawInputs(), validBody), expected)
                    );
                });

        // 3) body fuzz (params fissi = validi)
        Stream<DynamicTest> bodyFuzzTests = applyCustomBodyVectors(
                FuzzerGenerator.generateFuzzCases(validPayload).stream()
        )
                .filter(c -> !ignoredBodyFields.contains(c.fieldName()))
                .map(c -> {
                    int expected = bodyResolver.resolve(c);
                    return dynamicTest(
                            FuzzTestNameFormatter.fuzzName(c, expected),
                            () -> apiExecutor.accept(new RequestParts(validInputs, c.rawBody()), expected)
                    );
                });

        return Stream.of(Stream.of(validTest), paramsFuzzTests, bodyFuzzTests)
                .flatMap(s -> s);
    }

    private Stream<FuzzerGenerator.FuzzedCase> applyCustomBodyVectors(Stream<FuzzerGenerator.FuzzedCase> baseCases) {
        java.util.List<FuzzerGenerator.FuzzedCase> base = baseCases.toList();
        java.util.List<FuzzerGenerator.FuzzedCase> out = new java.util.ArrayList<>(base);

        // 1) Replace semantics: remove base cases if custom override exists for same field + fuzzId
        out.removeIf(baseCase -> {
            java.util.List<FuzzVectors<?>> customVectors = customBodyVectorsByField.get(baseCase.fieldName());
            if (customVectors == null || customVectors.isEmpty()) {
                return false;
            }
            return customVectors.stream().anyMatch(v -> v.id() == baseCase.fuzzId());
        });

        // 2) Add custom cases, using a template case of the same field to keep other fields intact
        for (FuzzerGenerator.FuzzedCase template : base) {
            java.util.List<FuzzVectors<?>> customVectors = customBodyVectorsByField.get(template.fieldName());
            if (customVectors == null || customVectors.isEmpty()) {
                continue;
            }

            // Avoid generating custom values from REQUIRED_MISSING templates
            if (template.fuzzId() == FuzzVectors.FuzzId.REQUIRED_MISSING) {
                continue;
            }

            for (FuzzVectors<?> vector : customVectors) {
                java.util.Map<String, Object> raw = new java.util.HashMap<>(template.rawBody());
                raw.put(template.fieldName(), vector.value());

                out.add(new FuzzerGenerator.FuzzedCase(
                        template.fieldName(),
                        vector.id(),
                        vector.value(),
                        vector.expectedStatus(),
                        vector.description(),
                        raw
                ));
            }
        }

        return out.stream();
    }
}