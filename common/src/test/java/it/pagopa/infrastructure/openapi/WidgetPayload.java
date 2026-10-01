package it.pagopa.infrastructure.openapi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WidgetPayload {
    private String name;
    private UUID id;
    private WidgetKind kind;
    private Integer count;
    private Long longCount;
    private Short shortCount;
    private Byte byteCount;
    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
    private Float floatValue;
    private Double doubleValue;
    private Boolean enabled;
    private WidgetDetail detail;
    private List<String> labels;
    private Set<WidgetDetail> details;
    private Collection<WidgetDetail> moreDetails;
    private List<Set<WidgetDetail>> nestedCollections;
    private Map<String, WidgetDetail> metadata;
    private WidgetPayload[] samples;
    private List<WidgetPayload> children;

    public WidgetPayload() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public WidgetKind getKind() { return kind; }
    public void setKind(WidgetKind kind) { this.kind = kind; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    public Long getLongCount() { return longCount; }
    public void setLongCount(Long longCount) { this.longCount = longCount; }
    public Short getShortCount() { return shortCount; }
    public void setShortCount(Short shortCount) { this.shortCount = shortCount; }
    public Byte getByteCount() { return byteCount; }
    public void setByteCount(Byte byteCount) { this.byteCount = byteCount; }
    public BigInteger getBigInteger() { return bigInteger; }
    public void setBigInteger(BigInteger bigInteger) { this.bigInteger = bigInteger; }
    public BigDecimal getBigDecimal() { return bigDecimal; }
    public void setBigDecimal(BigDecimal bigDecimal) { this.bigDecimal = bigDecimal; }
    public Float getFloatValue() { return floatValue; }
    public void setFloatValue(Float floatValue) { this.floatValue = floatValue; }
    public Double getDoubleValue() { return doubleValue; }
    public void setDoubleValue(Double doubleValue) { this.doubleValue = doubleValue; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public WidgetDetail getDetail() { return detail; }
    public void setDetail(WidgetDetail detail) { this.detail = detail; }
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public Set<WidgetDetail> getDetails() { return details; }
    public void setDetails(Set<WidgetDetail> details) { this.details = details; }
    public Collection<WidgetDetail> getMoreDetails() { return moreDetails; }
    public void setMoreDetails(Collection<WidgetDetail> moreDetails) { this.moreDetails = moreDetails; }
    public List<Set<WidgetDetail>> getNestedCollections() { return nestedCollections; }
    public void setNestedCollections(List<Set<WidgetDetail>> nestedCollections) { this.nestedCollections = nestedCollections; }
    public Map<String, WidgetDetail> getMetadata() { return metadata; }
    public void setMetadata(Map<String, WidgetDetail> metadata) { this.metadata = metadata; }
    public WidgetPayload[] getSamples() { return samples; }
    public void setSamples(WidgetPayload[] samples) { this.samples = samples; }
    public List<WidgetPayload> getChildren() { return children; }
    public void setChildren(List<WidgetPayload> children) { this.children = children; }
}
