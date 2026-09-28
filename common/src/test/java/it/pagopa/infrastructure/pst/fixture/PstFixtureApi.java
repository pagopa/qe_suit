package it.pagopa.infrastructure.pst.fixture;

public class PstFixtureApi {
    public UpdateResourceOper updateResource() {
        return new UpdateResourceOper();
    }

    public static class UpdateResourceOper {
        public UpdateResourceOper body(PstFixturePayload payload) {
            return this;
        }
    }
}
