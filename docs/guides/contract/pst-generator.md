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
    scope: PAYLOAD       # PAYLOAD | PATH_PARAMS | QUERY_PARAMS
    target: /delegationId
    scenario: REPLACED_WITH_NULL
    status: 200
```

## Request bodies

JSON bodies (`application/json`, `*+json`) are designed from the generated Java model resolved through the
`body(...)` method of the generated operation.

Form bodies (`multipart/form-data`, `application/x-www-form-urlencoded`) have no generated model: the
generator emits one `<field>Form(...)` method per field. They are designed as a typed field map that goes
through the same decomposer, rules, validity and expectation resolution as any other payload, so targets
appear as `$.<field>`. Binary fields (`format: binary`/`base64`) are not fuzzable and are excluded: the
contract test supplies the file directly on the generated operation. At runtime `OpenApiOperationAdapter`
binds these payloads field by field on the `<field>Form(...)` methods instead of setting a JSON body.

Any other media type (for example `application/octet-stream`) fails explicitly.

## Query parameters

Query parameters are a first class scope (`QUERY_PARAMS`), next to `PAYLOAD` and `PATH_PARAMS`. They are
designed as a typed parameter map built from the OpenAPI operation, so they go through the same decomposer,
rules, mutation validity and expectation resolution as the other scopes. Array parameters get one
representative element, so element level scenarios are designed too. Targets are rendered as `?name` and
`?name[0]`.

Query parameters have no generated DTO, hence no validation annotation: their only contract metadata is the
`required` flag of the specification, applied by `QueryParameterValidityResolver`. Removing or nulling an
optional parameter is therefore `VALID` (success expected), doing the same on a required one is `INVALID`.
Every other scenario stays `UNKNOWN` and is resolved by the policy, exactly like the other scopes.

At runtime `OpenApiOperationAdapter` binds the parameters on the generated `<name>Query(Object...)` methods,
expanding arrays into varargs. The runtime plans the query scope with the `queryParamsFuzzEngine`; when the
test does not declare the `required` flags, every declared parameter is treated as optional, which mirrors
the convention that a call without query parameters succeeds. The PST, which reads the specification, is
strictly more precise on required parameters.

## Known limitations

Top-level `List<Model>` bodies, immutable models, complex path parameters, external parameter `$ref` and
unresolvable inline enums in path parameters are not supported and fail explicitly.

Inline enums of form fields and of query parameters are designed as their base scalar type, because the generated form methods are
untyped (`Object...`) and no Java enum is generated for them: enum specific scenarios are therefore not
designed for those fields, consistently with what the runtime can actually send.
