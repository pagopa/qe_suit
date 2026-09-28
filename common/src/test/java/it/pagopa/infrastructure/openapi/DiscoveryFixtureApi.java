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

    public UploadWidgetDocumentOper uploadWidgetDocument() {
        return new UploadWidgetDocumentOper();
    }

    public SubmitWidgetFormOper submitWidgetForm() {
        return new SubmitWidgetFormOper();
    }

    public UploadWidgetRawOper uploadWidgetRaw() {
        return new UploadWidgetRawOper();
    }

    public SearchWidgetsOper searchWidgets() {
        return new SearchWidgetsOper();
    }

    public static class SearchWidgetsOper {
        public SearchWidgetsOper qQuery(Object... value) {
            return this;
        }

        public SearchWidgetsOper limitQuery(Object... value) {
            return this;
        }

        public SearchWidgetsOper statesQuery(Object... value) {
            return this;
        }
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

    public static class UploadWidgetDocumentOper {
        public UploadWidgetDocumentOper nameForm(Object... value) {
            return this;
        }

        public UploadWidgetDocumentOper revisionForm(Object... value) {
            return this;
        }

        public UploadWidgetDocumentOper kindForm(Object... value) {
            return this;
        }

        public UploadWidgetDocumentOper docMultiPart(java.io.File value) {
            return this;
        }
    }

    public static class SubmitWidgetFormOper {
        public SubmitWidgetFormOper codeForm(Object... value) {
            return this;
        }

        public SubmitWidgetFormOper activeForm(Object... value) {
            return this;
        }
    }

    public static class UploadWidgetRawOper {
    }
}
