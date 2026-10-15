package it.pagopa.utils;

/**
 * Utility methods used to format identifiers and textual values shared across different test modules.
 */
public class FormattingUtils {

    /**
     * Builds a numbered identifier using the provided prefix and description.
     *
     * @param number progressive identifier number
     * @param prefix identifier prefix
     * @param suffix scenario description
     * @return formatted identifier string
     */
    public static String enumerateTests(Integer number, String prefix, String suffix) {
        return String.format(
                "[%s-%02d] %s",
                prefix, number, suffix
        );
    }
}
