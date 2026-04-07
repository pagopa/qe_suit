package it.frontend.e2e.framework.web.model.location;

import it.frontend.e2e.framework.core.model.location.Location;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class Url implements Location {
    String url;

    @Override
    public void validate(String location) {
        throw new UnsupportedOperationException("Method validate() not implemented for " + this.getClass().getName());
    }
}
