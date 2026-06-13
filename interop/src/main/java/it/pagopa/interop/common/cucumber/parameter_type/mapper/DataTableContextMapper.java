package it.pagopa.interop.common.cucumber.parameter_type.mapper;

import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

//TODO: questo deve diventare uno strategy
@Component
public class DataTableContextMapper {
    private static final Map<String, Supplier<String>> dataTableTokens = new HashMap<>();
    private static final String functionName = "$retrieve";

    private final ScenarioContext scenarioContext;

    @Autowired
    public DataTableContextMapper(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        resolveDataTableToken();
    }

    public String resolve(String value) {
        if (value == null) return null;

        // Regex dinamica basata su functionName
        String regex = String.format("\\%s\\(([^)]+)\\)", functionName);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(value);

        StringBuilder sb = new StringBuilder();
        boolean found = false;

        while (matcher.find()) {
            found = true;
            String token = matcher.group(1).trim();
            Supplier<String> supplier = dataTableTokens.get(composeFunction(token));
            if (supplier == null) {
                throw new IllegalArgumentException("Funzione non riconosciuta o non risolvibile: " + matcher.group(0));
            }
            String replacement = supplier.get();
            matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return found ? sb.toString() : value;
    }

    private static String composeFunction(String token) {
        return functionName + "(" + token + ")";
    }

    private void resolveDataTableToken() {
        dataTableTokens.put(composeFunction("clientId"), () -> scenarioContext.getLastOrThrow(Client.class).getId().toString());
    }
}
