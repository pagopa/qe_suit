package it.pagopa.send;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

// l'ambiente arriva da spring.profiles.active (default test in application.yaml), come per i contract test
@CucumberContextConfiguration
@SpringBootTest(classes = TestBootApp.class, properties = "spring.profiles.include=cucumber")
public class CucumberSpringConfiguration {
}


