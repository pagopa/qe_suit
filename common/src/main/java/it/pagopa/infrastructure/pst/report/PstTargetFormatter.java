package it.pagopa.infrastructure.pst.report;

import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.objectgraph.NodePath;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Display-only rendering of a {@link NodePath} target. The model keeps the JSON Pointer; the report shows:
 * <ul>
 *     <li>PAYLOAD: JSONPath, e.g. {@code $}, {@code $.descriptor.id}, {@code $.items[0].name}, {@code $['a-b']};</li>
 *     <li>PATH_PARAMS: the path template placeholder, e.g. {@code {agreementId}}.</li>
 * </ul>
 * As in {@link NodePath#printable()}, an all-digit token is shown as an index.
 */
final class PstTargetFormatter {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_$][A-Za-z0-9_$]*");
    private static final Pattern INDEX = Pattern.compile("\\d+");

    private PstTargetFormatter() {
    }

    static String format(RequestScope scope, NodePath target) {
        List<String> tokens = tokens(target);
        if (scope == RequestScope.PATH_PARAMS && tokens.size() == 1) {
            return "{" + tokens.get(0) + "}";
        }
        return jsonPath(tokens);
    }

    private static String jsonPath(List<String> tokens) {
        StringBuilder path = new StringBuilder("$");
        for (String token : tokens) {
            if (INDEX.matcher(token).matches()) {
                path.append('[').append(token).append(']');
            } else if (IDENTIFIER.matcher(token).matches()) {
                path.append('.').append(token);
            } else {
                path.append("['").append(token.replace("\\", "\\\\").replace("'", "\\'")).append("']");
            }
        }
        return path.toString();
    }

    private static List<String> tokens(NodePath target) {
        List<String> tokens = new ArrayList<>();
        if (target.isRoot()) return tokens;
        for (String raw : target.toString().substring(1).split("/", -1)) {
            tokens.add(raw.replace("~1", "/").replace("~0", "~"));
        }
        return tokens;
    }
}
