package it.pagopa.infrastructure.reporting.contract.lifecycle;

public class ContractReportRunState {

    private boolean reportEnabled;

    public void enableReport() {
        this.reportEnabled = true;
    }

    public boolean reportEnabled() {
        return reportEnabled;
    }
}
