package it.pagopa.reporting.app;

import it.pagopa.reporting.dto.ReportDocument;
import it.pagopa.reporting.parser.ReportParser;
import it.pagopa.reporting.parser.ReportParsingException;
import it.pagopa.reporting.parser.surefire.SurefireXmlReportParser;
import it.pagopa.reporting.writer.ReportWriter;
import it.pagopa.reporting.writer.ReportWritingException;
import it.pagopa.reporting.writer.html.HtmlSidebarReportWriter;

public final class ReportingMain {

    private ReportingMain() {
    }

    public static void main(String[] args) {
        try {
            ReportingCliOptions options = ReportingCliOptions.parse(args);
            run(options);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (ReportParsingException | ReportWritingException e) {
            System.err.println("Reporting execution failed: " + e.getMessage());
            System.exit(1);
        }
    }

    static void run(ReportingCliOptions options) throws ReportParsingException, ReportWritingException {
        ReportParser parser = new SurefireXmlReportParser();
        ReportWriter writer = new HtmlSidebarReportWriter(options.includeDtoDump());

        ReportDocument reportDocument = parser.parse(options.inputPath());
        writer.write(reportDocument, options.outputPath());

        System.out.println("Report generated: " + options.outputPath());
    }
}

