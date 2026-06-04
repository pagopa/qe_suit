package it.pagopa.interop.bff.service.action;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AssertAction<Entity> {

    private ResponseEntity<Entity> response;

    AssertAction<Entity> handle(ResponseEntity<Entity> response) {
        this.response = response;
        return this;
    }

    public AssertAction<Entity> status(HttpStatusCode expectedStatus) {
        Assertions.assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus.value());
        return this;
    }

    public AssertAction<Entity> body(Consumer<ObjectAssert<Entity>> bodyAssert) {
        SoftAssertions softly = new SoftAssertions();
        bodyAssert.accept(softly.assertThat(response.getBody()));
        softly.assertAll();
        return this;
    }

    public Entity extract() {
        return response.getBody();
    }
}