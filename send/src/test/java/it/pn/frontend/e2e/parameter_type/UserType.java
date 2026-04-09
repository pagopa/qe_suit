package it.pn.frontend.e2e.parameter_type;

import io.cucumber.java.ParameterType;
import it.pn.frontend.e2e.enums.User;

public class UserType {

    @ParameterType("Grossini")
    public User user(String username) {
       return User.fromUsername(username);
    }
}
