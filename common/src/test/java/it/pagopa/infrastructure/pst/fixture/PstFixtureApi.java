package it.pagopa.infrastructure.pst.fixture;

public class PstFixtureApi {
    public UpdateResourceOper updateResource() {
        return new UpdateResourceOper();
    }

    public static class UpdateResourceOper {
        public UpdateResourceOper body(PstFixturePayload payload) {
            return this;
        }

        public UpdateResourceOper filterQuery(Object... value) {
            return this;
        }

        public UpdateResourceOper pageQuery(Object... value) {
            return this;
        }
    }
}
