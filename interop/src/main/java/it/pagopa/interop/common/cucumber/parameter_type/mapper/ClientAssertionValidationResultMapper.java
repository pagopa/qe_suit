package it.pagopa.interop.common.cucumber.parameter_type.mapper;

import io.cucumber.datatable.DataTable;
import it.pagopa.interop.common.domain.model.VoucherRequestValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class ClientAssertionValidationResultMapper {
    private final DataTableContextMapper dataTableContextMapper;

    public VoucherRequestValidationResult fromDataTable(DataTable dataTable) {
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

        Map<String, VoucherRequestValidationResult.ValidationResult> byStep = new HashMap<>();

        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);

            String step = row.get(stepIdx).trim();
            String result = row.get(resultIdx).trim();
            String errors = row.get(errorsIdx).trim();

            VoucherRequestValidationResult.Status status = parseStatus(result);
            boolean success = status == VoucherRequestValidationResult.Status.PASSED;
            List<String> errorsCode = this.parseErrors(errors);

            byStep.put(step, new VoucherRequestValidationResult.ValidationResult(status, success, errorsCode));
        }

        return new VoucherRequestValidationResult(
                null,
                new VoucherRequestValidationResult.ClientAssertionValidation(
                        require(byStep, "clientAssertionValidation")
                ),
                new VoucherRequestValidationResult.PublicKeyValidation(
                        require(byStep, "publicKeyRetrieve")
                ),
                new VoucherRequestValidationResult.SignatureValidation(
                        require(byStep, "clientAssertionSignatureVerification")
                ),
                byStep.containsKey("platformStatesVerification")
                        ? new VoucherRequestValidationResult.PlatformValidation(
                        require(byStep, "platformStatesVerification")
                ) : null,
                byStep.containsKey("dpopProofValidation")
                        ? new VoucherRequestValidationResult.DPoPValidation(
                        require(byStep, "dpopProofValidation")
                ) : null
        );
    }

    private static VoucherRequestValidationResult.ValidationResult require(
            Map<String, VoucherRequestValidationResult.ValidationResult> byStep,
            String key
    ) {
        VoucherRequestValidationResult.ValidationResult value = byStep.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Step mancante nella DataTable: " + key);
        }
        return value;
    }

    private static VoucherRequestValidationResult.Status parseStatus(String raw) {
        String value = normalize(raw);
        return switch (value) {
            case "passed" -> VoucherRequestValidationResult.Status.PASSED;
            case "failed" -> VoucherRequestValidationResult.Status.FAILED;
            case "skipped" -> VoucherRequestValidationResult.Status.SKIPPED;
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