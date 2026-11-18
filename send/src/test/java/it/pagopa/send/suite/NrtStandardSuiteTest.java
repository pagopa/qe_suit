package it.pagopa.send.suite;

import it.pagopa.infrastructure.reporting.contract.lifecycle.GenerateContractReport;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.SelectClasspathResources;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.EXECUTION_MODE_FEATURE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@GenerateContractReport
@IncludeEngines({"cucumber", "junit-jupiter"})
@SelectClasspathResources({
        @SelectClasspathResource("features-expanded")
})
// contract test (SpringBootTest + junit-jupiter): nessuna feature Cucumber li copre, vanno selezionati per package
// come in interop. "contract" è ricorsivo sulle sue sottodirectory (destinatario_pf/pg, partner_tecnologico);
// "authorization.bff" è un package a parte, non sotto "contract".
@SelectPackages({
        "it.pagopa.send.suite.contract",
        "it.pagopa.send.suite.authorization.bff"
})
@ConfigurationParameters({
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-reports/report.json," +
                "html:target/cucumber-reports/report.html"),
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "it.pagopa.send"),

        // abilita parallelismo JUnit (contract test junit-jupiter)
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "concurrent"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.config.strategy", value = "dynamic"),
        @ConfigurationParameter(key = "junit.jupiter.execution.parallel.config.dynamic.factor", value = "4"),

        // abilita parallelismo Cucumber
        @ConfigurationParameter(key = EXECUTION_MODE_FEATURE_PROPERTY_NAME, value = "concurrent"),

        // Filtro tag cucumber-only: a differenza di @IncludeTags (filtro globale JUnit Platform), non si applica
        // all'engine junit-jupiter, che quindi non perde i contract test selezionati sopra (non hanno @Tag).
        // Nessuna whitelist di tag business: qualunque nuova feature verrebbe esclusa per errore se non aggiunta
        // qui (è già successo: "loadPage"/"backstage" non esistono più in nessuna feature, e login.feature non ha
        // mai avuto un tag selezionabile). Si esclude solo ciò che non deve girare in questo ambiente.
        @ConfigurationParameter(
                key = FILTER_TAGS_PROPERTY_NAME,
                value = """
                        not @backofficePt
                        and not @ignore
                        """
        )
})
// @ExcludeTags è un filtro globale JUnit Platform (tutti gli engine): sicuro da tenere insieme al filtro
// cucumber-only sopra perché esclude solo chi ha il tag "ignore", senza toccare i test senza tag.
@ExcludeTags({"ignore"})
// TODO rimuovere "not @backofficePt" dal filtro quando l'NRT girerà su un ambiente con il BackOffice PT
// (oggi pn-pt-webapp esiste solo su dev)
public class NrtStandardSuiteTest {

}
