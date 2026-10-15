package it.pagopa.send;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = TestBootApp.class)
@ActiveProfiles({"test", "cucumber"})
public class CucumberSpringConfiguration {
}


