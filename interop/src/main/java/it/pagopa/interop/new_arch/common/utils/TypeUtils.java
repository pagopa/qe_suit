package it.pagopa.interop.new_arch.common.utils;

public final class TypeUtils {

    public static Integer safeParseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
