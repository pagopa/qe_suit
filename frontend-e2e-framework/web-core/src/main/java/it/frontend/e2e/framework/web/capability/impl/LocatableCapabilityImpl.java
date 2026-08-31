package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.capability.core.Locatable;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.location.Url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocatableCapabilityImpl extends AbstractCapabilityImpl implements Locatable {
    public LocatableCapabilityImpl(IWebPresentationApiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void navigateTo(String... pathParams) {
        Url url = urlSupplier.get();
        String urlTemplate = url.getUrl();

        Url result = resolveUrl(pathParams, urlTemplate);
        adapter.navigateTo(result);
    }

    protected Url resolveUrl(String[] pathParams, String urlTemplate) {
        Matcher matcher = Pattern.compile("\\$\\{[^}]+}").matcher(urlTemplate);

        int placeholdersCount = 0;
        while (matcher.find()) {
            placeholdersCount++;
        }

        if (placeholdersCount != pathParams.length) {
            throw new IllegalArgumentException(
                    "Path parameters mismatch: expected " + placeholdersCount
                            + ", provided " + pathParams.length
            );
        }

        matcher = Pattern.compile("\\$\\{[^}]+}").matcher(urlTemplate);

        StringBuilder resolvedUrl = new StringBuilder();
        int index = 0;

        while (matcher.find()) {
            matcher.appendReplacement(
                    resolvedUrl,
                    Matcher.quoteReplacement(pathParams[index++])
            );
        }

        matcher.appendTail(resolvedUrl);
        return Url.of(resolvedUrl.toString());
    }
}
