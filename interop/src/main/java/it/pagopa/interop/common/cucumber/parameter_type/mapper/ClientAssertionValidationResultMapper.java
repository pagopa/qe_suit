package it.pagopa.interop.common.cucumber.parameter_type.mapper;

import io.cucumber.datatable.DataTable;
import it.pagopa.interop.common.domain.model.ClientAssertionValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class ClientAssertionValidationResultMapper {
    private final DataTableContextMapper dataTableContextMapper;

    public ClientAssertionValidationResult fromDataTable(DataTable dataTable) {
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
            List<String> errorsCode = this.parseErrors(errors);

            byStep.put(step, new ClientAssertionValidationResult.ValidationResult(status, success, errorsCode));
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
                byStep.containsKey("platformStatesVerification")
                        ? new ClientAssertionValidationResult.PlatformValidation(
                        require(byStep, "platformStatesVerification")
                ) : null,
                byStep.containsKey("dpopProofValidation")
                        ? new ClientAssertionValidationResult.DPoPValidation(
                        require(byStep, "dpopProofValidation")
                ) : null
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

    private List<String> parseErrors(String raw) {
        if (raw == null || raw.trim().isEmpty() || "[]".equals(raw.trim())) {
            return List.of();
        }
        String value = raw.trim();
        if (value.startsWith("[") && value.endsWith("]")) {
            String inner = value.substring(1, value.length() - 1).trim();
            if (inner.isEmpty()) return List.of();
            // Split per virgola, risolvi ogni errore tramite dataTableContextMapper
            return Arrays.stream(inner.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(dataTableContextMapper::resolve)
                    .toList();
        }
        // Risolvi anche il caso singolo
        return List.of(dataTableContextMapper.resolve(value));
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}