package it.pagopa.interop.new_arch.common.infrastructure.cucumber.resolver;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class DataTableExpressionResolver {
    private final Map<String, DataTableResolutionStrategy> strategies;
    private final Pattern regexPattern;

    public DataTableExpressionResolver(List<DataTableResolutionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(DataTableResolutionStrategy::getFunctionName, s -> s));

        // Creiamo una regex dinamica che cattura qualsiasi funzione censita: \$(blank|uuid|today)\(([^)]*)\)
        String funzioniAllegate = String.join("|", strategies.keySet());
        String regex = String.format("\\$(%s)\\(([^)]*)\\)", funzioniAllegate);
        this.regexPattern = Pattern.compile(regex);
    }

    public String resolve(String value) {
        if (value == null || strategies.isEmpty()) return value;

        Matcher matcher = regexPattern.matcher(value);
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        while (matcher.find()) {
            found = true;
            String functionName = matcher.group(1);
            String argument = matcher.group(2).trim();

            DataTableResolutionStrategy strategy = strategies.get(functionName);
            String replacement = strategy.resolve(argument);

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return found ? sb.toString() : value;
    }
}
