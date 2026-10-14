package it.pagopa.infrastructure.openapi;

import java.util.Optional;

public class OptionalSeedFixture {
    private Optional<WidgetDetail> detail;

    public OptionalSeedFixture() {
    }

    public Optional<WidgetDetail> getDetail() { return detail; }
    public void setDetail(Optional<WidgetDetail> detail) { this.detail = detail; }
}
