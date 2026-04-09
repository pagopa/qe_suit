package it.pagopa.send.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.send.enums.User;

public class UserType {

    @ParameterType("Grossini")
    public User user(String username) {
       return User.fromUsername(username);
    }
}
