package it.pagopa.reporting.parser;

import it.pagopa.reporting.dto.ReportDocument;

import java.nio.file.Path;

public interface ReportParser {
    ReportDocument parse(Path inputPath) throws ReportParsingException;
}

