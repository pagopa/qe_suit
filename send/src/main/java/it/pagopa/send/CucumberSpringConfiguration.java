package it.pagopa.send;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = TestBootApp.class)
public class CucumberSpringConfiguration {
}


