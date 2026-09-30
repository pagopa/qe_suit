package it.pagopa.interop.common.client.infrastructure.cucumber;

import it.pagopa.interop.common.client.domain.DebugClientAssertionValidation;

import java.util.Arrays;
import java.util.List;

public record ValidationExpected(
        ValidationStep step,
        DebugClientAssertionValidation.Status result,
        String errors
) {

    public List<String> errorsAsList() {
        if (errors == null || errors.isBlank()) {
            return List.of();
        }

        return Arrays.stream(errors.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }
}
