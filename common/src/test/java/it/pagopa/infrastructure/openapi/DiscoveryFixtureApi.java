package it.pagopa.infrastructure.openapi;

import java.util.List;

public class DiscoveryFixtureApi {

    public CreateWidgetOper createWidget() {
        return new CreateWidgetOper();
    }

    public CreateWidgetBatchOper createWidgetBatch() {
        return new CreateWidgetBatchOper();
    }

    public GetWidgetOper getWidget() {
        return new GetWidgetOper();
    }

    public GetWidgetBySlugOper getWidgetBySlug() {
        return new GetWidgetBySlugOper();
    }

    public GetWidgetByKindOper getWidgetByKind() {
        return new GetWidgetByKindOper();
    }

    public GetWidgetMetricsOper getWidgetMetrics() {
        return new GetWidgetMetricsOper();
    }

    public GetWidgetByInlineKindOper getWidgetByInlineKind() {
        return new GetWidgetByInlineKindOper();
    }

    public static class CreateWidgetOper {
        public CreateWidgetOper body(WidgetPayload body) {
            return this;
        }
    }

    public static class CreateWidgetBatchOper {
        public CreateWidgetBatchOper body(List<WidgetPayload> body) {
            return this;
        }
    }

    public static class GetWidgetOper {
    }

    public static class GetWidgetBySlugOper {
    }

    public static class GetWidgetByKindOper {
    }

    public static class GetWidgetMetricsOper {
    }

    public static class GetWidgetByInlineKindOper {
    }
}
