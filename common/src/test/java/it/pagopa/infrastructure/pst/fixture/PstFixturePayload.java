package it.pagopa.infrastructure.pst.fixture;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class PstFixturePayload {
    private String simple;
    @Size(min = 2, max = 20)
    private String sized;
    private UUID id;
    private PstFixtureKind kind;
    private Integer count;
    @Nullable
    private String nullable;
    @JsonProperty(required = false)
    private String optional;
    @JsonProperty(required = true)
    @NotNull
    private String required;
    private PstFixtureNested nested;
    private List<PstFixtureNested> items;

    public PstFixturePayload() {
    }

    public String getSimple() { return simple; }
    public void setSimple(String simple) { this.simple = simple; }
    public String getSized() { return sized; }
    public void setSized(String sized) { this.sized = sized; }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public PstFixtureKind getKind() { return kind; }
    public void setKind(PstFixtureKind kind) { this.kind = kind; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public String getNullable() { return nullable; }
    public void setNullable(String nullable) { this.nullable = nullable; }
    public String getOptional() { return optional; }
    public void setOptional(String optional) { this.optional = optional; }
    public String getRequired() { return required; }
    public void setRequired(String required) { this.required = required; }
    public PstFixtureNested getNested() { return nested; }
    public void setNested(PstFixtureNested nested) { this.nested = nested; }
    public List<PstFixtureNested> getItems() { return items; }
    public void setItems(List<PstFixtureNested> items) { this.items = items; }
}
