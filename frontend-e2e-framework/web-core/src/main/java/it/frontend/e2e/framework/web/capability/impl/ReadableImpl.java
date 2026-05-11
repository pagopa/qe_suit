package it.frontend.e2e.framework.web.capability.impl;

import it.frontend.e2e.framework.core.assertion.AssertionAction;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.model.WebPresentationElement;

import java.util.List;
import java.util.Optional;

public class ReadableImpl<T> extends AbstractCapabilityImpl implements Readable<T> {

    private final Class<T> type;

    public ReadableImpl(IWebPresentationApiAdapter adapter, Class<T> type) {
        super(adapter);
        this.type = type;
    }

    @Override
    public T read() {
        Optional<WebPresentationElement> elementOpt = adapter.findElement(xPathSelector.get());
        if (elementOpt.isEmpty()) return null;
        String text = elementOpt.get().getText();
        return convert(text, type);
    }

    @Override
    public T readAndAssert(T expected) {
        var value = read();

        if (!expected.equals(value))
            throw new AssertionError("Expected " + expected + " found " + value);

        return value;
    }

    @Override
    public T readAndAssert(AssertionAction<T> assertionAction) {
        var value = read();
        assertionAction.assertOn(value);
        return value;
    }

    @Override
    public List<T> readAll() {
        var elementsOpt = adapter.findElements(xPathSelector.get());
        return elementsOpt.map(webPresentationElements -> webPresentationElements.stream()
                .map(e -> convert(e.getText(), type))
                .toList()).orElseGet(List::of);
    }

    @Override
    public List<T> readAllAndAssert(List<T> expected) {
        var values = readAll();
        if (!values.equals(expected)) {
            throw new AssertionError("Expected list " + expected + " found " + values);
        }
        return values;
    }

    @Override
    public List<T> readAllAndAssert(AssertionAction<List<T>> assertionAction) {
        var values = readAll();
        assertionAction.assertOn(values);
        return values;
    }

    @SuppressWarnings("unchecked")
    private T convert(String text, Class<T> type) {
        if (type == String.class) {
            return (T) text;
        } else if (type == Integer.class) {
            return (T) Integer.valueOf(text);
        } else if (type == Long.class) {
            return (T) Long.valueOf(text);
        } else if (type == Double.class) {
            return (T) Double.valueOf(text);
        } else if (type == Float.class) {
            return (T) Float.valueOf(text);
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(text);
        } else if (type == Short.class) {
            return (T) Short.valueOf(text);
        } else if (type == Byte.class) {
            return (T) Byte.valueOf(text);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
}