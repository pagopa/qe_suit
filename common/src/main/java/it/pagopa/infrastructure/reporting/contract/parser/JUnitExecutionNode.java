package it.pagopa.infrastructure.reporting.contract.parser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JUnitExecutionNode {

    private final String id;
    private String parentId;
    private String uniqueId;
    private String displayName;
    private String junitType;
    private String className;
    private String methodName;
    private Instant startedAt;
    private Instant finishedAt;
    private JUnitExecutionStatus status = JUnitExecutionStatus.UNKNOWN;
    private JUnitThrowableData throwable;
    private String skipReason;
    private final List<JUnitExecutionNode> children = new ArrayList<>();

    public JUnitExecutionNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getJunitType() {
        return junitType;
    }

    public void setJunitType(String junitType) {
        this.junitType = junitType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public JUnitExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(JUnitExecutionStatus status) {
        this.status = status;
    }

    public JUnitThrowableData getThrowable() {
        return throwable;
    }

    public void setThrowable(JUnitThrowableData throwable) {
        this.throwable = throwable;
    }

    public String getSkipReason() {
        return skipReason;
    }

    public void setSkipReason(String skipReason) {
        this.skipReason = skipReason;
    }

    public List<JUnitExecutionNode> getChildren() {
        return children;
    }

    public void addChild(JUnitExecutionNode child) {
        this.children.add(child);
    }
}
