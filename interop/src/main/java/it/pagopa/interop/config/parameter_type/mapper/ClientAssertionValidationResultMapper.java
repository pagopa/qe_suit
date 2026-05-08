package it.pagopa.interop.config.parameter_type.mapper;

import io.cucumber.datatable.DataTable;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ClientAssertionValidationResultMapper {

    private ClientAssertionValidationResultMapper() {
    }

    public static ClientAssertionValidationResult fromDataTable(DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();
        if (rows == null || rows.size() < 2) {
            throw new IllegalArgumentException("DataTable vuota o senza righe dati");
        }

        List<String> header = rows.get(0).stream()
                .map(ClientAssertionValidationResultMapper::normalize)
                .toList();

        int stepIdx = header.indexOf("step");
        int resultIdx = header.indexOf("result");
        int errorsIdx = header.indexOf("errors");

        if (stepIdx < 0 || resultIdx < 0 || errorsIdx < 0) {
            throw new IllegalArgumentException("Header DataTable non valido. Attesi: step, result, errors");
        }

        Map<String, ClientAssertionValidationResult.ValidationResult> byStep = new HashMap<>();

        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);

            String step = row.get(stepIdx).trim();
            String result = row.get(resultIdx).trim();
            String errors = row.get(errorsIdx).trim();

            ClientAssertionValidationResult.Status status = parseStatus(result);
            boolean success = status == ClientAssertionValidationResult.Status.PASSED;
            String errorCode = parseErrors(errors);

            byStep.put(step, new ClientAssertionValidationResult.ValidationResult(status, success, errorCode));
        }

        return new ClientAssertionValidationResult(
                new ClientAssertionValidationResult.ClientAssertionValidation(
                        require(byStep, "clientAssertionValidation")
                ),
                new ClientAssertionValidationResult.PublicKeyValidation(
                        require(byStep, "publicKeyRetrieve")
                ),
                new ClientAssertionValidationResult.SignatureValidation(
                        require(byStep, "clientAssertionSignatureVerification")
                ),
                new ClientAssertionValidationResult.PlatformValidation(
                        require(byStep, "platformStatesVerification")
                )
        );
    }

    private static ClientAssertionValidationResult.ValidationResult require(
            Map<String, ClientAssertionValidationResult.ValidationResult> byStep,
            String key
    ) {
        ClientAssertionValidationResult.ValidationResult value = byStep.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Step mancante nella DataTable: " + key);
        }
        return value;
    }

    private static ClientAssertionValidationResult.Status parseStatus(String raw) {
        String value = normalize(raw);
        return switch (value) {
            case "passed" -> ClientAssertionValidationResult.Status.PASSED;
            case "failed" -> ClientAssertionValidationResult.Status.FAILED;
            case "skipped" -> ClientAssertionValidationResult.Status.SKIPPED;
            default -> throw new IllegalArgumentException("Result non valido: " + raw);
        };
    }

    private static String parseErrors(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty() || "[]".equals(value)) {
            return null;
        }
        if (value.startsWith("[") && value.endsWith("]")) {
            String inner = value.substring(1, value.length() - 1).trim();
            return inner.isEmpty() ? null : inner;
        }
        return value;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}