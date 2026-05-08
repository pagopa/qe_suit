package it.frontend.e2e.framework.web.model;


import it.frontend.e2e.framework.core.model.AbstractPresentationElement;
import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.model.location.Url;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WebPresentationElement extends AbstractPresentationElement<XPathSelector, Url> {
    private String tag;
    private String text;
    private Map<String, String> attributes;
    private List<WebPresentationElement> children;

    public WebPresentationElement(XPathSelector selector, Url location) {
        super(selector, location);
    }

    public List<String> getClasses() {
    String classAttribute = attributes.getOrDefault("class", "");
    if (classAttribute.isBlank()) {
        return List.of();
    }
    return Arrays.asList(classAttribute.split("\\s+"));
}
}
