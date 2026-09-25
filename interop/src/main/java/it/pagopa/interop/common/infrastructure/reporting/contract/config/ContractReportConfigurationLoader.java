package it.pagopa.interop.common.infrastructure.reporting.contract.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContractReportConfigurationLoader {

    private static final String DEFAULT_CLASSPATH_RESOURCE = "application.yaml";
    private final Path applicationYamlPath;

    public ContractReportConfigurationLoader() {
        this(null);
    }

    public ContractReportConfigurationLoader(Path applicationYamlPath) {
        this.applicationYamlPath = applicationYamlPath;
    }

    public ContractReportConfig load() {
        try (InputStream inputStream = openStream()) {
            Object loaded = new Yaml().load(inputStream);
            Map<String, Object> root = asMap(loaded, "application.yaml root");
            Map<String, Object> contractReport = asMap(root.get("contract-report"), "contract-report");
            Map<String, Object> channels = asMap(contractReport.get("channels"), "contract-report.channels");
            Map<String, ContractChannelConfig> mappedChannels = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : channels.entrySet()) {
                String channelKey = entry.getKey();
                Map<String, Object> channel = asMap(entry.getValue(), "contract-report.channels." + channelKey);
                ContractChannelConfig config = new ContractChannelConfig(
                        channelKey,
                        toNullableString(channel.get("label")),
                        toNullableString(channel.get("class-prefix")),
                        readTargetType(channel.get("target-type"), channelKey),
                        toNullableString(channel.get("openapi"))
                );
                mappedChannels.put(channelKey, config);
            }
            return new ContractReportConfig(mappedChannels);
        } catch (IOException ex) {
            String source = applicationYamlPath == null ? "classpath:" + DEFAULT_CLASSPATH_RESOURCE : applicationYamlPath.toString();
            throw new IllegalStateException("Cannot read configuration file: " + source, ex);
        }
    }

    private InputStream openStream() throws IOException {
        if (applicationYamlPath != null) {
            if (!Files.exists(applicationYamlPath)) {
                throw new IllegalStateException("Configuration file not found: " + applicationYamlPath);
            }
            return Files.newInputStream(applicationYamlPath);
        }
        InputStream classpathStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_CLASSPATH_RESOURCE);
        if (classpathStream == null) {
            throw new IllegalStateException("Configuration resource not found in classpath: " + DEFAULT_CLASSPATH_RESOURCE);
        }
        return classpathStream;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value, String name) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new IllegalStateException(name + " must be an object map");
    }

    private ContractTargetType readTargetType(Object raw, String channelKey) {
        if (!(raw instanceof String targetType)) {
            throw new IllegalStateException("contract-report.channels." + channelKey + ".target-type is required");
        }
        return ContractTargetType.fromRaw(targetType);
    }

    private String toNullableString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
