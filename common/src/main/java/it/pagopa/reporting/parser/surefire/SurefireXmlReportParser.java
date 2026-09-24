package it.pagopa.reporting.parser.surefire;

import it.pagopa.reporting.dto.ConcreteCaseReport;
import it.pagopa.reporting.dto.EnvironmentProperty;
import it.pagopa.reporting.dto.ExecutionStatus;
import it.pagopa.reporting.dto.ReportDocument;
import it.pagopa.reporting.dto.RunSummary;
import it.pagopa.reporting.dto.StatusCounts;
import it.pagopa.reporting.dto.TestClassReport;
import it.pagopa.reporting.dto.TestFactoryReport;
import it.pagopa.reporting.parser.ReportParser;
import it.pagopa.reporting.parser.ReportParsingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SurefireXmlReportParser implements ReportParser {

    private static final Pattern FACTORY_PATTERN = Pattern.compile("^(?<factory>.+)\\(\\)\\[(?<idx>\\d+)]$");
    private static final Pattern CASE_MARKER_PATTERN = Pattern.compile("(?<scenario>[A-Z][A-Z0-9_]+)\\s+@\\s+(?<target><root>|/[^\\s:]+)");
    private static final Pattern THREAD_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\[(?<thread>[^]]+)]");
    private static final Pattern FAILURE_SCENARIO_PATTERN = Pattern.compile("(?m)^\\s*-\\s*scenario:\\s*(?<scenario>[A-Z][A-Z0-9_]+)\\s*$");
    private static final Pattern FAILURE_TARGET_PATTERN = Pattern.compile("(?m)^\\s*-\\s*targetPath:\\s*(?<target>[^\\r\\n]+)\\s*$");
    private static final Pattern PRIVATE_PATH_PATTERN = Pattern.compile("([A-Za-z]:\\\\|/Users/|/home/|\\\\AppData\\\\|\\\\.m2\\\\repository|/\\.m2/repository)");

    private static final Set<String> ALLOWED_PROPERTY_KEYS = Set.of(
            "java.specification.version",
            "java.version",
            "java.runtime.name",
            "java.vm.name",
            "java.vendor",
            "os.name",
            "os.arch",
            "os.version",
            "sun.cpu.isalist",
            "sun.cpu.endian",
            "file.separator",
            "path.separator",
            "line.separator",
            "java.awt.headless",
            "sun.management.compiler",
            "jdk.debug",
            "APPLICATION_NAME",
            "LOGGED_APPLICATION_NAME",
            "user.language",
            "user.country",
            "user.timezone",
            "file.encoding",
            "native.encoding",
            "sun.jnu.encoding",
            "FILE_LOG_CHARSET",
            "CONSOLE_LOG_CHARSET",
            "test",
            "java.specification.name",
            "java.specification.vendor",
            "java.specification.maintenance.version",
            "java.vm.specification.vendor",
            "java.vm.specification.name",
            "java.vm.specification.version",
            "java.vm.version",
            "java.runtime.version",
            "java.version.date",
            "java.class.version",
            "java.vm.info",
            "java.vm.compressedOopsMode",
            "sun.java.launcher",
            "sun.os.patch.level",
            "sun.io.unicode.encoding",
            "java.vendor.url",
            "java.vendor.url.bug",
            "java.vendor.version"
    );

    @Override
    public ReportDocument parse(Path inputPath) throws ReportParsingException {
        List<Path> reportFiles = resolveReportFiles(inputPath);
        if (reportFiles.isEmpty()) {
            throw new ReportParsingException("No Surefire XML report files found under: " + inputPath);
        }

        long tests = 0;
        long failures = 0;
        long errors = 0;
        long skipped = 0;
        double durationSeconds = 0;

        List<String> warnings = new ArrayList<>();
        List<ParsedTestcase> parsedTestcases = new ArrayList<>();
        Map<String, String> safeProperties = new LinkedHashMap<>();
        Set<String> excludedPropertyKeys = new LinkedHashSet<>();

        for (Path reportFile : reportFiles) {
            Element suite = parseTestsuite(reportFile);

            tests += parseLongAttribute(suite, "tests");
            failures += parseLongAttribute(suite, "failures");
            errors += parseLongAttribute(suite, "errors");
            skipped += parseLongAttribute(suite, "skipped");
            durationSeconds += parseDoubleAttribute(suite, "time");

            extractSafeProperties(suite, safeProperties, excludedPropertyKeys, warnings, reportFile);
            parsedTestcases.addAll(parseTestcases(suite));
        }

        List<TestClassReport> classReports = buildClassReports(parsedTestcases);
        List<EnvironmentProperty> properties = safeProperties.entrySet().stream()
                .map(entry -> new EnvironmentProperty(entry.getKey(), entry.getValue()))
                .toList();
        List<String> excludedProperties = excludedPropertyKeys.stream()
                .sorted()
                .toList();

        RunSummary summary = new RunSummary(tests, failures, errors, skipped, durationSeconds);
        return new ReportDocument(summary, properties, excludedProperties, classReports, warnings);
    }

    private List<Path> resolveReportFiles(Path inputPath) throws ReportParsingException {
        if (inputPath == null) {
            throw new ReportParsingException("Input path is required");
        }

        if (!Files.exists(inputPath)) {
            throw new ReportParsingException("Input path does not exist: " + inputPath);
        }

        try {
            if (Files.isRegularFile(inputPath)) {
                return List.of(inputPath.toAbsolutePath().normalize());
            }

            try (Stream<Path> stream = Files.walk(inputPath)) {
                return stream
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().startsWith("TEST-"))
                        .filter(path -> path.getFileName().toString().endsWith(".xml"))
                        .sorted()
                        .map(path -> path.toAbsolutePath().normalize())
                        .toList();
            }
        } catch (IOException e) {
            throw new ReportParsingException("Cannot read input path: " + inputPath, e);
        }
    }

    private Element parseTestsuite(Path reportFile) throws ReportParsingException {
        try {
            DocumentBuilderFactory factory = newSecureDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(reportFile.toFile());
            Element root = document.getDocumentElement();

            if (root == null || !"testsuite".equals(root.getTagName())) {
                throw new ReportParsingException("Root tag <testsuite> expected in file: " + reportFile);
            }

            return root;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ReportParsingException("Cannot parse Surefire XML file: " + reportFile, e);
        }
    }

    private DocumentBuilderFactory newSecureDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory;
    }

    private void extractSafeProperties(
            Element suite,
            Map<String, String> safeProperties,
            Set<String> excludedPropertyKeys,
            List<String> warnings,
            Path reportFile
    ) {
        NodeList propertyNodes = suite.getElementsByTagName("property");
        for (int i = 0; i < propertyNodes.getLength(); i++) {
            Node node = propertyNodes.item(i);
            if (!(node instanceof Element property)) {
                continue;
            }

            String key = property.getAttribute("name");
            String value = property.getAttribute("value");

            if (!shouldKeepProperty(key, value, excludedPropertyKeys)) {
                continue;
            }

            String previous = safeProperties.putIfAbsent(key, value);
            if (previous != null && !Objects.equals(previous, value)) {
                warnings.add("Property '" + key + "' has multiple values across files. Kept first value from earlier report.");
                warnings.add("Conflicting file: " + reportFile.getFileName());
            }
        }
    }

    private boolean shouldKeepProperty(String key, String value, Set<String> excludedPropertyKeys) {
        if (key == null || key.isBlank()) {
            return false;
        }

        if (!ALLOWED_PROPERTY_KEYS.contains(key)) {
            excludedPropertyKeys.add(key);
            return false;
        }

        if (value == null || value.isBlank()) {
            return true;
        }

        boolean containsPrivatePath = PRIVATE_PATH_PATTERN.matcher(value).find();
        if (containsPrivatePath) {
            excludedPropertyKeys.add(key);
        }
        return !containsPrivatePath;
    }

    private List<ParsedTestcase> parseTestcases(Element suite) {
        NodeList testcaseNodes = suite.getElementsByTagName("testcase");
        List<ParsedTestcase> result = new ArrayList<>();

        for (int i = 0; i < testcaseNodes.getLength(); i++) {
            Node node = testcaseNodes.item(i);
            if (!(node instanceof Element testcase)) {
                continue;
            }

            String className = orDefault(testcase.getAttribute("classname"), "UNKNOWN_CLASS");
            String testcaseName = orDefault(testcase.getAttribute("name"), "UNKNOWN_TESTCASE");
            double testcaseDuration = parseDoubleAttribute(testcase, "time");
            ExecutionStatus testcaseStatus = extractStatus(testcase);

            Element failureElement = firstDirectChildByTagName(testcase, "failure");
            Element errorElement = firstDirectChildByTagName(testcase, "error");
            Element statusElement = failureElement != null ? failureElement : errorElement;
            FailureData failureData = extractFailureData(statusElement);

            String systemOut = directChildrenByTagName(testcase, "system-out").stream()
                    .map(Element::getTextContent)
                    .collect(Collectors.joining("\n"));

            FactoryIdentity factoryIdentity = extractFactoryIdentity(testcaseName);
            List<ConcreteCaseReport> concreteCases = extractConcreteCases(
                    testcaseName,
                    factoryIdentity,
                    testcaseDuration,
                    testcaseStatus,
                    failureData,
                    systemOut
            );

            result.add(new ParsedTestcase(
                    className,
                    factoryIdentity.factoryName,
                    testcaseName,
                    testcaseStatus,
                    testcaseDuration,
                    concreteCases
            ));
        }

        return result;
    }

    private FactoryIdentity extractFactoryIdentity(String testcaseName) {
        Matcher matcher = FACTORY_PATTERN.matcher(testcaseName);
        if (!matcher.matches()) {
            // Non-dynamic testcase names (without [index]) represent factory-level execution nodes.
            return new FactoryIdentity(testcaseName, null, false);
        }

        Integer index = null;
        try {
            index = Integer.parseInt(matcher.group("idx"));
        } catch (NumberFormatException ignored) {
            // ignore malformed index and keep null
        }

        return new FactoryIdentity(matcher.group("factory"), index, true);
    }

    private List<ConcreteCaseReport> extractConcreteCases(
            String testcaseName,
            FactoryIdentity factoryIdentity,
            double testcaseDuration,
            ExecutionStatus testcaseStatus,
            FailureData failureData,
            String systemOut
    ) {
        if (!factoryIdentity.dynamicCase()) {
            // Preserve factory-level failures/errors as explicit cases, without marker splitting.
            return List.of(buildSingleCase(
                    testcaseName,
                    testcaseName,
                    factoryIdentity,
                    testcaseDuration,
                    testcaseStatus,
                    failureData,
                    systemOut
            ));
        }

        String failureKey = failureCaseKey(failureData);
        List<ConcreteCaseReport> concreteCases = new ArrayList<>();

        if (systemOut == null || systemOut.isBlank()) {
            concreteCases.add(buildSingleCase(
                    testcaseName,
                    "UNKNOWN_CASE",
                    factoryIdentity,
                    testcaseDuration,
                    testcaseStatus,
                    failureData,
                    ""
            ));
            return concreteCases;
        }

        Map<String, StringBuilder> logsByCase = new LinkedHashMap<>();
        Map<String, String> activeCaseByThread = new HashMap<>();
        StringBuilder unassignedLog = new StringBuilder();

        String[] lines = systemOut.split("\\R", -1);
        for (String line : lines) {
            String thread = threadName(line).orElse("GLOBAL");
            Matcher markerMatcher = CASE_MARKER_PATTERN.matcher(line);

            if (markerMatcher.find()) {
                String caseKey = caseKey(markerMatcher.group("scenario"), markerMatcher.group("target"));
                activeCaseByThread.put(thread, caseKey);
                appendLine(logsByCase.computeIfAbsent(caseKey, ignored -> new StringBuilder()), line);
                continue;
            }

            String activeCase = activeCaseByThread.get(thread);
            if (activeCase != null) {
                appendLine(logsByCase.computeIfAbsent(activeCase, ignored -> new StringBuilder()), line);
            } else {
                appendLine(unassignedLog, line);
            }
        }

        if (logsByCase.isEmpty()) {
            concreteCases.add(buildSingleCase(
                    testcaseName,
                    "UNKNOWN_CASE",
                    factoryIdentity,
                    testcaseDuration,
                    testcaseStatus,
                    failureData,
                    systemOut
            ));
            return concreteCases;
        }

        int caseOrdinal = 1;
        for (Map.Entry<String, StringBuilder> entry : logsByCase.entrySet()) {
            String key = entry.getKey();
            String scenario = scenarioFromCaseKey(key);
            String target = targetFromCaseKey(key);
            ExecutionStatus caseStatus = inferCaseStatus(testcaseStatus, logsByCase.size(), key, failureKey);

            concreteCases.add(new ConcreteCaseReport(
                    testcaseName + "#" + caseOrdinal,
                    key,
                    scenario,
                    target,
                    caseStatus,
                    testcaseName,
                    factoryIdentity.index,
                    testcaseDuration,
                    failureData.failureType,
                    failureData.failureMessage,
                    failureData.stackTrace,
                    entry.getValue().toString().trim()
            ));
            caseOrdinal++;
        }

        if (!unassignedLog.toString().isBlank()) {
            concreteCases.add(new ConcreteCaseReport(
                    testcaseName + "#" + caseOrdinal,
                    "UNKNOWN_CASE",
                    "UNKNOWN_CASE",
                    null,
                    testcaseStatus == ExecutionStatus.PASSED ? ExecutionStatus.PASSED : ExecutionStatus.UNKNOWN,
                    testcaseName,
                    factoryIdentity.index,
                    testcaseDuration,
                    failureData.failureType,
                    failureData.failureMessage,
                    failureData.stackTrace,
                    unassignedLog.toString().trim()
            ));
        }

        return concreteCases;
    }

    private ConcreteCaseReport buildSingleCase(
            String testcaseName,
            String displayName,
            FactoryIdentity factoryIdentity,
            double testcaseDuration,
            ExecutionStatus testcaseStatus,
            FailureData failureData,
            String caseLog
    ) {
        String normalizedLog = caseLog == null ? "" : caseLog.trim();

        return new ConcreteCaseReport(
                testcaseName + "#1",
                displayName,
                displayName,
                null,
                testcaseStatus,
                testcaseName,
                factoryIdentity.index,
                testcaseDuration,
                failureData.failureType,
                failureData.failureMessage,
                failureData.stackTrace,
                normalizedLog
        );
    }

    private Optional<String> threadName(String line) {
        Matcher matcher = THREAD_PATTERN.matcher(line);
        return matcher.find() ? Optional.ofNullable(matcher.group("thread")) : Optional.empty();
    }

    private String failureCaseKey(FailureData failureData) {
        if (failureData == null) {
            return null;
        }

        String corpus = (failureData.failureMessage == null ? "" : failureData.failureMessage)
                + "\n"
                + (failureData.stackTrace == null ? "" : failureData.stackTrace);

        Matcher scenarioMatcher = FAILURE_SCENARIO_PATTERN.matcher(corpus);
        Matcher targetMatcher = FAILURE_TARGET_PATTERN.matcher(corpus);

        if (!scenarioMatcher.find() || !targetMatcher.find()) {
            return null;
        }

        return caseKey(scenarioMatcher.group("scenario"), targetMatcher.group("target").trim());
    }

    private ExecutionStatus inferCaseStatus(
            ExecutionStatus testcaseStatus,
            int extractedCases,
            String currentCaseKey,
            String failureCaseKey
    ) {
        if (testcaseStatus == ExecutionStatus.PASSED || testcaseStatus == ExecutionStatus.SKIPPED) {
            return testcaseStatus;
        }

        if (extractedCases == 1) {
            return testcaseStatus;
        }

        if (failureCaseKey != null && failureCaseKey.equals(currentCaseKey)) {
            return testcaseStatus;
        }

        return ExecutionStatus.UNKNOWN;
    }

    private List<TestClassReport> buildClassReports(List<ParsedTestcase> parsedTestcases) {
        Map<String, ClassAccumulator> classes = new LinkedHashMap<>();

        for (ParsedTestcase testcase : parsedTestcases) {
            ClassAccumulator classAccumulator = classes.computeIfAbsent(testcase.className, ignored -> new ClassAccumulator());
            classAccumulator.statusBuilder.add(testcase.status);
            classAccumulator.durationSeconds += testcase.durationSeconds;

            FactoryAccumulator factoryAccumulator = classAccumulator.factories.computeIfAbsent(
                    testcase.factoryName,
                    ignored -> new FactoryAccumulator()
            );
            factoryAccumulator.statusBuilder.add(testcase.status);
            factoryAccumulator.durationSeconds += testcase.durationSeconds;
            factoryAccumulator.concreteCases.addAll(testcase.concreteCases);
        }

        List<String> sortedClassNames = new ArrayList<>(classes.keySet());
        sortedClassNames.sort(String::compareTo);

        List<TestClassReport> classReports = new ArrayList<>();
        for (String className : sortedClassNames) {
            ClassAccumulator classAccumulator = classes.get(className);

            List<String> sortedFactoryNames = new ArrayList<>(classAccumulator.factories.keySet());
            sortedFactoryNames.sort(String::compareTo);

            List<TestFactoryReport> factories = new ArrayList<>();
            for (String factoryName : sortedFactoryNames) {
                FactoryAccumulator factoryAccumulator = classAccumulator.factories.get(factoryName);
                List<ConcreteCaseReport> sortedCases = factoryAccumulator.concreteCases.stream()
                        .sorted(Comparator
                                .comparing(ConcreteCaseReport::sourceIndex, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(ConcreteCaseReport::id))
                        .toList();

                factories.add(new TestFactoryReport(
                        factoryName,
                        factoryAccumulator.statusBuilder.build(),
                        factoryAccumulator.durationSeconds,
                        sortedCases
                ));
            }

            classReports.add(new TestClassReport(
                    className,
                    classAccumulator.statusBuilder.build(),
                    classAccumulator.durationSeconds,
                    factories
            ));
        }

        return classReports;
    }

    private FailureData extractFailureData(Element statusElement) {
        if (statusElement == null) {
            return new FailureData(null, null, null);
        }

        String type = emptyToNull(statusElement.getAttribute("type"));
        String message = emptyToNull(statusElement.getAttribute("message"));
        String stackTrace = emptyToNull(statusElement.getTextContent());

        return new FailureData(type, message, stackTrace);
    }

    private ExecutionStatus extractStatus(Element testcase) {
        if (firstDirectChildByTagName(testcase, "failure") != null) {
            return ExecutionStatus.FAILED;
        }
        if (firstDirectChildByTagName(testcase, "error") != null) {
            return ExecutionStatus.ERROR;
        }
        if (firstDirectChildByTagName(testcase, "skipped") != null) {
            return ExecutionStatus.SKIPPED;
        }
        return ExecutionStatus.PASSED;
    }

    private static String caseKey(String scenario, String targetPath) {
        return scenario + " @ " + targetPath;
    }

    private static String scenarioFromCaseKey(String caseKey) {
        int separator = caseKey.indexOf(" @ ");
        return separator < 0 ? caseKey : caseKey.substring(0, separator);
    }

    private static String targetFromCaseKey(String caseKey) {
        int separator = caseKey.indexOf(" @ ");
        return separator < 0 ? null : caseKey.substring(separator + 3);
    }

    private static void appendLine(StringBuilder builder, String line) {
        if (!builder.isEmpty()) {
            builder.append('\n');
        }
        builder.append(line);
    }

    private static String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private static String orDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private long parseLongAttribute(Element element, String attributeName) {
        try {
            String raw = element.getAttribute(attributeName);
            if (raw == null || raw.isBlank()) {
                return 0;
            }
            return Long.parseLong(raw);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private double parseDoubleAttribute(Element element, String attributeName) {
        try {
            String raw = element.getAttribute(attributeName);
            if (raw == null || raw.isBlank()) {
                return 0;
            }
            return Double.parseDouble(raw);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private Element firstDirectChildByTagName(Element parent, String tagName) {
        for (Element child : directChildrenByTagName(parent, tagName)) {
            return child;
        }
        return null;
    }

    private List<Element> directChildrenByTagName(Element parent, String tagName) {
        List<Element> matches = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element element && tagName.equals(element.getTagName())) {
                matches.add(element);
            }
        }
        return matches;
    }

    private record FailureData(String failureType, String failureMessage, String stackTrace) {
    }

    private record FactoryIdentity(String factoryName, Integer index, boolean dynamicCase) {
    }

    private record ParsedTestcase(
            String className,
            String factoryName,
            String testcaseName,
            ExecutionStatus status,
            double durationSeconds,
            List<ConcreteCaseReport> concreteCases
    ) {
    }

    private static final class ClassAccumulator {
        private final StatusCounts.Builder statusBuilder = new StatusCounts.Builder();
        private final Map<String, FactoryAccumulator> factories = new LinkedHashMap<>();
        private double durationSeconds;
    }

    private static final class FactoryAccumulator {
        private final StatusCounts.Builder statusBuilder = new StatusCounts.Builder();
        private final List<ConcreteCaseReport> concreteCases = new ArrayList<>();
        private double durationSeconds;
    }
}

