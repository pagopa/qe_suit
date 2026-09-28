package it.pagopa.infrastructure.pst.launcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzingProfile;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.openapi.OpenApiOperationDiscovery;
import it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory;
import it.pagopa.infrastructure.openapi.seed.OperationSeedFactory;
import it.pagopa.infrastructure.pst.PstConfig;
import it.pagopa.infrastructure.pst.PstConfigurationException;
import it.pagopa.infrastructure.pst.PstGenerator;
import it.pagopa.infrastructure.pst.model.PstDocument;
import it.pagopa.infrastructure.pst.model.PstOperation;
import it.pagopa.infrastructure.pst.report.PstReportRenderer;

import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * Wires the existing PST pipeline components. Contains no planning, validity or expectation logic:
 * rules and mapper come exclusively from the supplied {@link FuzzingProfile}.
 */
public final class PstGenerationFacade {

    private final ClassLoader classLoader;

    public PstGenerationFacade(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader must not be null");
    }

    public PstGenerationResult generate(PstGenerationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        if (!Files.isRegularFile(request.configPath()) || !Files.isReadable(request.configPath())) {
            throw new PstConfigurationException(
                    "PST configuration file does not exist or is not readable: " + request.configPath());
        }
        PstConfig config = PstConfig.load(request.configPath());

        FuzzingProfile profile = request.fuzzingProfile();
        ObjectMapper objectMapper = Objects.requireNonNull(
                profile.objectMapper(), "FuzzingProfile.objectMapper() must not return null");
        PstGenerator generator = new PstGenerator(
                new OpenApiOperationDiscovery(classLoader),
                request.apiConfiguration(),
                new OperationSeedFactory(new DeterministicSeedFactory()),
                new DefaultObjectGraphDecomposer(new JacksonObjectDecomposer(objectMapper)),
                objectMapper,
                Objects.requireNonNull(profile.payloadPlanner(), "FuzzingProfile.payloadPlanner() must not return null"),
                Objects.requireNonNull(profile.pathParamsPlanner(), "FuzzingProfile.pathParamsPlanner() must not return null")
        );

        PstDocument document = generator.generate(config);
        new PstReportRenderer().render(document, request.title(), request.outputPath());

        int scenarioCount = document.operations().stream()
                .map(PstOperation::scenarios)
                .mapToInt(List::size)
                .sum();
        return new PstGenerationResult(request.outputPath(), document.operations().size(), scenarioCount);
    }
}
