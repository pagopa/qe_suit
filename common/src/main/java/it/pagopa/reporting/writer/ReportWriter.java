package it.pagopa.reporting.writer;

import it.pagopa.reporting.dto.ReportDocument;

import java.nio.file.Path;

public interface ReportWriter {
    void write(ReportDocument reportDocument, Path outputPath) throws ReportWritingException;
}

