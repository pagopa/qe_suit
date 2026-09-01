package it.pagopa.infrastructure.cucumber.channel;

import java.nio.file.Path;

@FunctionalInterface
public interface ChannelScenarioExpansion {
    String expand(Path sourceFile, String sourceText);
}