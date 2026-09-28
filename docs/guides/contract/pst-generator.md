# PST generator (`pst:generate`)

The PST (Progettazione Scenari di Test) generator produces, **before** the contract tests are written, an HTML
report with the fuzz scenarios, mutation validity and expected status that the runtime contract tests will produce
for the same API.

PST and runtime share the same pipeline:

```
OpenAPI Generator execution (effective MavenProject)
  -> GeneratedApiConfiguration
  -> OpenApiOperationDiscovery (reflection on generated API/model classes)
  -> deterministic seed (body model / typed path params)
  -> ObjectGraphDecomposer
  -> FuzzCasePlanner (from the FuzzingProfile)
  -> JacksonMutationValidityResolver
  -> ExpectationResolver
  -> PstDocument
  -> PstReportRenderer (HTML)
```

## Invocation

From the product module (e.g. `interop`):

```bash
mvn pst:generate \
  -Dpst.openapiExecution=generate-bff-client \
  -Dpst.config=src/test/resources/pst/bff.yaml \
  -Dpst.title="[BFF] - Contract Test Scenarios"
```

Prerequisites: `common` and `pst-maven-plugin` installed in the local repository
(`mvn -f common/pom.xml install`, `mvn -f pst-maven-plugin/pom.xml install`).

The report is written to `target/pst/<openapiExecution>/pst-report.html`
(e.g. `target/pst/generate-bff-client/pst-report.html`).

## Parameters

| Property | Required | Description |
|---|---|---|
| `pst.openapiExecution` | yes | Id of the `org.openapitools:openapi-generator-maven-plugin` execution to design. |
| `pst.config` | yes | PST YAML configuration (relative to the project base directory). |
| `pst.fuzzingProfile` | yes | FQCN of the `FuzzingProfile` shared with the runtime. In `interop` it is set as project property. |
| `pst.output` | no | Report path override. |
| `pst.title` | no | Report title. Default: `Progettazione Scenari di Test`. |

`inputSpec`, `apiPackage` and `modelPackage` are read from the selected execution of the effective project model
(`apiPackage`/`modelPackage` either top-level or inside `configOptions`). Missing, ambiguous or conflicting values
fail the build explicitly; there is no fallback to another execution.

## Lifecycle

The goal declares `@Execute(phase = COMPILE)`: invoking it directly forks the lifecycle up to `compile` in the same
Maven process (codegen, source patches, compilation), so it works after `clean` and never depends on a previous
build under `target`. The goal is not bound to any phase: normal builds never generate the PST.

`mvn compile pst:generate` also works but compiles twice because of the fork; the direct goal is the normal usage.
The test phases are not executed, so no external services, credentials or Spring context are needed. Network access
is required only when `inputSpec` is a remote URL.

## FuzzingProfile

`it.pagopa.infrastructure.fuzzing.FuzzingProfile` is the single source of truth for the `ObjectMapper` and the
payload / path-params `FuzzCasePlanner`s. The runtime Spring configuration delegates to the same profile
(in `interop`: `InteropFuzzingProfile`, used by `FuzzingConfig` and `JacksonConfig`), so changing a rule changes both
runtime and PST. A profile must have a public no-arg constructor and must not require external services.

## Adding a new API

1. Add the OpenAPI Generator execution (e.g. `generate-m2m-client`).
2. Add the PST YAML (all `FuzzScenario` values must have a status).
3. If the runtime uses different rules, add a `FuzzingProfile` and wire the runtime to it.
4. `mvn pst:generate -Dpst.openapiExecution=generate-m2m-client -Dpst.config=... [-Dpst.fuzzingProfile=...]`

No change to the PST framework is required.

## PST YAML

```yaml
successStatus: 200
scenarioStatus:          # one entry for every FuzzScenario value
  REPLACED_WITH_NULL: 400
  # ...
operations:              # optional, default: all operations
  - createAgreement
overrides:               # optional
  - operationId: createAgreement
    scope: PAYLOAD       # PAYLOAD | PATH_PARAMS
    target: /delegationId
    scenario: REPLACED_WITH_NULL
    status: 200
```

## Known limitations

Top-level `List<Model>` bodies, immutable models, complex path parameters, external parameter `$ref` and
unresolvable inline enums are not supported and fail explicitly.
