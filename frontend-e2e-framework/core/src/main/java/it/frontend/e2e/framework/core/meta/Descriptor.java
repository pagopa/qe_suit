package it.frontend.e2e.framework.core.meta;

import java.util.List;
import java.util.Map;

public final class Descriptor {
    public int schemaVersion;
    public String domainId;
    public Map<String, String> entities;
    public Map<String, Scope> scopes;
    public List<Constraint> constraints;
}

