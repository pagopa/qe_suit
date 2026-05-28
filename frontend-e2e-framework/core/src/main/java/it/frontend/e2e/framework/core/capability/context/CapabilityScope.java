package it.frontend.e2e.framework.core.capability.context;

public record CapabilityScope(String selector, String location, boolean collection) {
        @Override
        public String toString() {
            String sel = selector != null && !selector.isEmpty() ? "Selector: " + selector : "";
            String loc = location != null && !location.isEmpty() ? "Location: " + location : "";
            return (sel + " " + loc).trim();
        }
}
