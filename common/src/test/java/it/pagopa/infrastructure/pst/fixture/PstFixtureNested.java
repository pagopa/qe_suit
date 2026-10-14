package it.pagopa.infrastructure.pst.fixture;

import jakarta.validation.constraints.Size;

public class PstFixtureNested {
    @Size(min = 2)
    private String value;

    public PstFixtureNested() {
    }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
